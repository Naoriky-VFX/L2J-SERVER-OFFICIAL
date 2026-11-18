#!/bin/sh

error_out() {
  >&2 echo "$*"
}

info_out() {
  echo "$*"
}

usage_out() {
  info_out "Usage: make_docker_image.sh OPTIONS"
  info_out "  OPTIONS:"
  info_out "    -h          Show this help."
  info_out "    -i          Execute the script in interactive mode."
  info_out "    -l <branch> Loginserver branch. Defaults to 'master'."
  info_out "    -u <user>   The user on the docker hub."
  info_out "    -n <name>   The image name. Defaults to 'l2j-server-login'."
  info_out "    -t <tag>    The tag of the image to build. Defaults to 'latest'."
  info_out "    -r          Fully rebuild the docker image."
}

input_in() {
  if [ $INTERACTIVE -eq 1 ]; then
    read -p "$1 " $3
  else
    eval "$3='$2'"
  fi
}

build_docker_image() {
  if [ $FULLY_REBUILD -eq 1 ]; then
    docker build -f Dockerfile -t "$1" --pull "$2" --build-arg "L2JLOGIN_BRANCH=$3" --no-cache
  else
    docker build -f Dockerfile -t "$1" --pull "$2" --build-arg "L2JLOGIN_BRANCH=$3"
  fi
}

. ./parse_opts.sh

INTERACTIVE="0"
L2JLOGIN_BRANCH="master"
L2J_IMAGE_USER=""
L2J_IMAGE_NAME="l2j-server-login"
L2J_IMAGE_TAG="latest"
FULLY_REBUILD="0"

[ ! -z $OPT_i ] && INTERACTIVE="1"
[ ! -z $OPT_l ] && L2JLOGIN_BRANCH="$OPT_l"
[ ! -z $OPT_u ] && L2J_IMAGE_USER="$OPT_u"
[ ! -z $OPT_n ] && L2J_IMAGE_NAME="$OPT_n"
[ ! -z $OPT_t ] && L2J_IMAGE_TAG="$OPT_t"
[ ! -z $OPT_r ] && FULLY_REBUILD=1

if [ ! -z $OPT_h ]; then
  usage_out
  return 0
fi

[ $INTERACTIVE -eq 1 ] && echo "Running in interactive mode..."

L2J_IMAGE_FULL_NAME="$L2J_IMAGE_NAME:$L2J_IMAGE_TAG"
[ ! -z $L2J_IMAGE_USER ] && L2J_IMAGE_FULL_NAME="$L2J_IMAGE_USER/$L2J_IMAGE_FULL_NAME"

info_out "LS Branch: $L2JLOGIN_BRANCH"
info_out "Full image tag: $L2J_IMAGE_FULL_NAME"
input_in "Continue? (Y/N)" "Y" CONFIRMATION

if [ "$CONFIRMATION" = "Y" ]; then
  build_docker_image "$L2J_IMAGE_FULL_NAME" "." "$L2JLOGIN_BRANCH"
fi
