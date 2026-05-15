# 2025-3e-VirtuálníBankovnictví

dokumentace:
- pdf: [Dokumentace.pdf](Dokumentace.pdf)
- MS word: [Dokumentace.docx](Dokumentace.docx)

## Jak spustit projekt
### Obsah:
- Nainstalovat PostgreSQL
- Nainstalovat Docker Desktop
- Přepnout Docker do linuxových kontejnerů
- V příkazovém řádku spustit command docker pull postgres
- Stáhnout soubory
- Kontrola
- Spustit

#### Nainstalovat PostgreSQL
Z https://www.postgresql.org/download/windows/ nainstalovat installer
nainstalovat nejnovější verzi.

#### Nainstalovat Docker Desktop
Z https://docs.docker.com/desktop/setup/install/windows-install/ naistalovat Docker Desktop for Windows - x86_64

#### Přepnout Docker do linuxových kontejnerů
Spustit docker a po spuštění mezi aplikacemi na pozadí pravým kliknout na ikonu dockeru a přepnout na linux (pokud tam vidíte “přepnout na windows kontejnery” znamená to, že je máte na linuxu).


#### V příkazovém řádku spustit command docker pull postgres
Otevřít jakýkoliv příkazový řádek (powershell, git bash, …) a spustit “docker pull postgres”.

#### Stáhnout soubory
https://drive.google.com/drive/u/1/folders/1IykAMwJC6cpNXdfM9g0f1FIxAEmuoPLN
Stáhněte soubory “DockerVirtualniBankovnictvi.yml” a “start.bat” do stejného adresáře.
“DockerVirtualniBankovnictvi.yml” je nastavení pro db.
“start.bat” je spouštění db v dockeru.

#### Kontrola
Ujistit se jestli máte stejné připojení na databázi (jméno, port, ...).

#### Spouštění
Pokud jste nespustili "start.bat" udělejte to a můžete zapnout projekt (v Application.java spustit main).

