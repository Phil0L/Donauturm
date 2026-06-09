# Donauturm

Eine Android-App für den **JKP Donauturm e.V.** in Ulm – entwickelt, um den Vereinsalltag zu unterstützen und das Erlebnis vor Ort zu verbessern.

---

## Features

| Modul | Beschreibung |
|---|---|
| 📅 **Kalender** | Veranstaltungen und Termine des Jugendzentrums auf einen Blick |
| 🍹 **Getränkekarte** | Digitale Karte mit dem aktuellen Getränkeangebot |
| ✅ **Checkliste** | Praktische Checklisten für Events und Aufgaben |
| 🎱 **Bingo** | Bingo-Spiel für Veranstaltungen |
| 🎵 **Spotify-Integration** | Musiksteuerung direkt über die App |
| 💬 **WhatsApp-Anbindung** | Direkte WhatsApp-Kommunikation aus der App |
| 🖥️ **PiSignage** | Steuerung von Infodisplays im Jugendzentrum via Raspberry Pi |

---

## Technologien

- **Plattform:** Android
- **Sprache:** Java
- **Build-System:** Gradle
- **Integrationen:** Spotify API, WhatsApp, PiSignage API

---

## Installation & Setup

### Voraussetzungen

- Android Studio
- Android Gerät

### Projekt klonen

```bash
git clone https://github.com/Phil0L/Donauturm.git
cd Donauturm
```

### In Android Studio öffnen

1. Android Studio starten
2. „Open an existing project" wählen
3. Den geklonten Ordner auswählen
4. Gradle-Sync abwarten
5. App auf einem Gerät oder Emulator starten

---

## Projektstruktur

```
Donauturm/
├── app/              # Haupt-App-Modul
├── bingo/            # Bingo-Spiel
├── calendar/         # Kalender & Termine
├── checklist/        # Checklisten für Vereinsinterne Abläufe
├── commons/          # Gemeinsam genutzte Hilfsfunktionen
├── drinksmenu/       # Getränkekarte
├── dtspotify/        # Spotify-Integration zur Musiksteuereung
├── dtwhatsapp/       # WhatsApp-Anbindung
├── fonts/            # Schriftarten
├── pisignageapi/     # PiSignage API-Modul zur Steuerung der Signage Monitore
└── spotify/          # Spotify-Kernmodul
```

---

## Mitwirken

Beiträge sind willkommen! So geht's:

1. Repository forken
2. Feature-Branch erstellen (`git checkout -b feature/mein-feature`)
3. Änderungen committen (`git commit -m 'Add: mein Feature'`)
4. Branch pushen (`git push origin feature/mein-feature`)
5. Pull Request öffnen

---

## Über den JKP Donauturm e.V.

Der [JKP Donauturm e.V.](https://www.jkp-donauturm.de) ist ein Jugendzentrum in Ulm. Diese App wurde von Mitgliedern des Vereins entwickelt, um den Betrieb vor Ort digital zu unterstützen.

---

## 📄 Lizenz

Dieses Projekt ist nicht öffentlich lizenziert. Bei Fragen zur Nutzung bitte direkt Kontakt aufnehmen.
