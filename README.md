# 🚀 SMB Fileshare com Docker — Ambiente Genérico

Este repositório traz um ambiente prático de **servidor SMB (Samba) em container Docker**, pronto para integração com qualquer aplicação (Java, Python, etc) e para testes manuais via linha de comando.

---

## 📂 Estrutura do Projeto

```text
project-root/
├── Dockerfile
├── docker-compose.yml
└── fileshare/         # Pasta local mapeada no compartilhamento SMB
```

---

## 🐳 Dockerfile do Samba

```dockerfile
FROM dperson/samba

RUN mkdir -p /smb/fileshare && chmod 0777 /smb/fileshare
```

---

## 🐳 docker-compose.yml

```yaml
version: "3.8"
services:
  samba:
    build: .
    image: samba-fileshare
    container_name: samba-fileshare
    hostname: smb-server
    environment:
      - USERID=1000
      - GROUPID=1000
    ports:
      - "137:137/udp"
      - "138:138/udp"
      - "139:139"
      - "445:445"
    command: >
      -p
      -s "fileshare;/smb/fileshare;yes;no;yes;smbuser;smbpass"
      -u "smbuser;smbpass"
    volumes:
      - ./fileshare:/smb/fileshare
    tty: true
```

---

## ▶️ Subindo o Container Samba

```bash
docker-compose up --build -d
```

---

## 🛠️ Testando Upload/Download de Arquivos com smbclient

Você pode testar o acesso SMB usando outro container (Alpine) com smbclient:

```bash
# Inicie um shell num container Alpine na mesma rede do Samba
docker run -it --rm --network container:samba-fileshare alpine /bin/sh

# Instale o smbclient
apk add --no-cache samba-client

# Crie um arquivo de teste
echo "Conteudo de teste SMB" > arquivo_envio.txt

# Envie o arquivo para o compartilhamento
smbclient //smb-server/fileshare -U smbuser%smbpass -c "put arquivo_envio.txt"

# Liste os arquivos no compartilhamento
smbclient //smb-server/fileshare -U smbuser%smbpass -c "ls"

# Baixe o arquivo testado
smbclient //smb-server/fileshare -U smbuser%smbpass -c "get arquivo_envio.txt"

# Verifique o conteúdo
cat arquivo_envio.txt
```

---

## 🌐 Testando a Aplicação Spring Boot REST

Após validar o compartilhamento SMB, teste os endpoints da aplicação Spring Boot (em `localhost:8080`):

**Upload de arquivo**
```bash
curl -v -F "file=@/caminho/para/seuarquivo.txt" http://localhost:8080/upload
```

**Download de arquivo**
```bash
curl -v -O -J "http://localhost:8080/download?filename=seuarquivo.txt"
```

---

## ⚙️ Resumo de Configuração

- **Usuário SMB:** `smbuser`
- **Senha:** `smbpass`
- **Compartilhamento:** `fileshare`
- **Hostname SMB:** `smb-server`
- **Diretório local persistido:** `./fileshare`

---

## 💡 Dicas

- O diretório `fileshare` será acessível na máquina host.
- Você pode instalar o `smbclient` em Linux/macOS para testar do host.
- Integre com aplicações Java (Spring Boot + JCIFS-NG), Python (pysmb) ou qualquer stack com suporte SMB.

---

Pronto! Seu ambiente está configurado para integração e experimentação SMB em containers Docker.
