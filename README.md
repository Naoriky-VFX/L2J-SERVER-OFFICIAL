# L2J Server — oficial (espelho automático)

Espelho diário do **L2J oficial**, que vive no Bitbucket (`bitbucket.org/l2jserver`),
e não no GitHub. O repositório antigo do GitHub (`github.com/L2J`) está abandonado
desde 2019 e o próprio projeto avisa para usar o Bitbucket.

Atualizado automaticamente **todo dia** pelo GitHub Actions (`sync.yml`), que busca
as últimas versões no Bitbucket e faz commit aqui.

## O que tem aqui

| Pasta | Repositório oficial | O que é |
|---|---|---|
| `game/` | [l2j-server-game](https://bitbucket.org/l2jserver/l2j-server-game) | Core do servidor (game server), em Java |
| `datapack/` | [l2j-server-datapack](https://bitbucket.org/l2jserver/l2j-server-datapack) | NPCs, quests, scripts e dados do mundo |
| `login/` | [l2j-server-login](https://bitbucket.org/l2jserver/l2j-server-login) | Login server (autenticação das contas) |
| `commons/` | [l2j-server-commons](https://bitbucket.org/l2jserver/l2j-server-commons) | Biblioteca comum do game e do login |

## Como compilar (resumo)

```bash
# precisa de Java (JDK) e Ant
cd game
ant        # gera o build em game/build
cd ../login
ant
```

O `datapack` é carregado pelo game server em tempo de execução.

## Licença

O L2J é distribuído sob **GPLv3** — este espelho mantém a licença e os créditos
originais (arquivos `LICENSE`, cabeçalhos e autores de cada projeto).

## Fonte

- Bitbucket oficial: https://bitbucket.org/l2jserver
- Site do projeto: https://www.l2jserver.com *(obs.: o DNS desse domínio está apontando
  para 8.8.8.8 em 12/11/2026, então não abre)*
