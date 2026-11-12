# L2J Server — Guia de Instalação (português)

> Copiado do guia oficial (`l2jserver.com/pt/docs/installation-guide`) em 12/11/2026.
> O domínio está fora do ar (DNS apontando para 8.8.8.8), então o texto foi recuperado
> pelo arquivo: https://web.archive.org/web/20260313053957/https://www.l2jserver.com/pt/docs/installation-guide

O L2J é dividido em **Game Server** (Core + Datapack) e **Login Server**.
O Core é o Java com a mecânica do jogo; o Datapack são diálogos, scripts, AIs, etc.

## 1. Software necessário

- Sistema atualizado
- **Java (JDK)** — o L2J acompanha a última LTS
- **MariaDB** (recomendado) ou MySQL 8.0+
- **Git**
- Para gerenciar o banco: HeidiSQL ou DBeaver (opcional)

Windows (winget):

```powershell
winget install EclipseAdoptium.Temurin.25.JDK
winget install -e --id Git.Git
winget install -e --id MariaDB.Server
```

Confira:

```powershell
java --version
git --version
```

Crie o usuário do banco:

```sql
CREATE OR REPLACE USER 'l2j'@'%' IDENTIFIED BY 'l2jserver2019';
GRANT ALL PRIVILEGES ON *.* TO 'l2j'@'%' IDENTIFIED BY 'l2jserver2019';
FLUSH PRIVILEGES;
```

> Se mudar a senha, edite `config/server.properties` do game e do login.

## 2. Código fonte

```powershell
mkdir C:\opt\l2j\git
cd C:\opt\l2j\git

git clone -b master  https://bitbucket.org/l2jserver/l2j-server-login.git
git clone -b develop https://bitbucket.org/l2jserver/l2j-server-game.git
git clone -b develop https://bitbucket.org/l2jserver/l2j-server-datapack.git
```

> `develop` = versão mais recente (High Five). `master` = estável.
> Existem outros ramos: Interlude, Epilogue, Freya…

## 3. Compilar (Maven)

```powershell
cd C:\opt\l2j\git\l2j-server-login
.\mvnw install

cd C:\opt\l2j\git\l2j-server-game
.\mvnw install

cd C:\opt\l2j\git\l2j-server-datapack
.\mvnw install
```

> Aviso do NVD API Key: para o build não demorar, pegue uma chave em
> https://nvd.nist.gov/developers/request-an-api-key e defina `NVD_API_KEY`.

## 4. Implantar

```powershell
mkdir C:\opt\l2j\server\login
cd C:\opt\l2j\server\login
tar -xf C:\opt\l2j\git\l2j-server-login\target\l2j-server-login-*.zip

mkdir C:\opt\l2j\server\game
cd C:\opt\l2j\server\game
tar -xf C:\opt\l2j\git\l2j-server-game\target\l2j-server-game-*.zip
tar -xf C:\opt\l2j\git\l2j-server-datapack\target\l2j-server-datapack-*.zip
```

> Os nomes dos arquivos mudam a cada versão.

## 5. Banco de dados (L2J CLI)

```powershell
mkdir C:\opt\l2j\cli
cd C:\opt\l2j\cli
curl -o l2jcli.zip -L https://l2jserver.com/api/download/cli/latest
tar -xf l2jcli.zip
```

> ⚠️ O download do CLI vem do site oficial, que está fora do ar.
> Alternativa: buildar o CLI do repositório `l2j-server-cli` no Bitbucket.

Edite `login-server.properties` e `game-server.properties` (DatabaseUser / DatabasePassword)
e rode:

```powershell
l2jcli.bat
db install -sql C:\opt\l2j\server\login\sql -u l2j -p l2jserver2019 -m FULL -t LOGIN -c -mods
db install -sql C:\opt\l2j\server\game\sql  -u l2j -p l2jserver2019 -m FULL -t GAME  -c -mods
```

Parâmetros: `-sql` caminho, `-u` usuário, `-p` senha, `-m` FULL|UPDATE,
`-t` LOGIN|GAME, `-c` tabelas personalizadas, `-mods` tabelas de mods.

Criar conta admin (8 = nível máximo):

```
account create -u SeuNome -p -a 8
quit
```

## 6. Portas (se for jogar de fora)

```powershell
netsh advfirewall firewall add rule name="L2J Login Server" dir=in action=allow protocol=TCP localport=2106
netsh advfirewall firewall add rule name="L2J Game Server"  dir=in action=allow protocol=TCP localport=7777
```

## 7. Geodata

https://bitbucket.org/l2jgeo/l2j_geodata/get/master.zip

## 8. Iniciar

```powershell
C:\opt\l2j\server\login\startLoginServer.bat
C:\opt\l2j\server\game\startGameServer.bat
```

## 9. Conectar

No `C:\Windows\System32\drivers\etc\hosts`:

```
127.0.0.1 l2authd.lineage2.com
```

Ou um `.bat`:

```bat
@start l2.bin IP=127.0.0.1
```

> ⚠️ Modificações no cliente não são permitidas no L2J (GameGuard, L2.ini).
