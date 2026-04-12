# 🏴‍☠️ Devil Fruit Mod - Istruzioni per la Compilazione

Questa mod aggiunge i frutti del diavolo (Magu Magu, Hie Hie, Goro Goro) a Minecraft 1.20.1.
Poiché hai scaricato il codice sorgente, devi "compilarlo" per ottenere il file `.jar` da inserire nella cartella `mods`.

## 🛠 Requisiti
1. **Java 17**: Assicurati di avere installato il Java Development Kit (JDK) 17. Puoi scaricarlo da [Adoptium (Temurin)](https://adoptium.net/temurin/releases/?version=17).
2. **Connessione Internet**: Necessaria solo la prima volta per scaricare le librerie di Minecraft Forge.

## 🚀 Come creare il file JAR

### 1. Scarica e Estrai
Scarica lo ZIP del progetto e estrailo in una cartella sul tuo PC.

### 2. Compila
Apri il terminale (o il Prompt dei Comandi / PowerShell) all'interno della cartella estratta e scrivi:

**Su Windows (PowerShell o CMD):**
```powershell
.\gradlew build
```

**Su Linux o macOS:**
```bash
chmod +x gradlew
./gradlew build
```

### 3. Prendi la Mod
Una volta terminato il processo (apparirà la scritta `BUILD SUCCESSFUL`), troverai il file pronto qui:
`build/libs/devilfruitmod-1.0.0.jar`

Copia questo file nella cartella `.minecraft/mods` e sei pronto a giocare!

---

## 🎮 Comandi in gioco
- **R**: Attiva/Disattiva Logia (Invulnerabilità e effetti passivi)
- **Z**: Abilità 1
- **X**: Abilità 2
- **C**: Abilità 3
