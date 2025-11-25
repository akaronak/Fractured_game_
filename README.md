Fractured – A Psychological Thriller Text Adventure (Java)

Fractured is a Java-based psychological thriller that takes players inside the fractured mind of Elias, a man haunted by memories, voices, and illusions. Guided by an ominous entity called The Lapati, the player must navigate shifting scenes, solve narrative puzzles, manage sanity, and uncover the truth about Elias’s past.

This project features a modular act-based structure, a custom-built game engine, inventory and sanity systems, persistent saving with SQLite, and multi-threaded background events.

🌌 Features
🎭 Act-Based Story Architecture

-The story is divided into Acts (Act1–Act6), each with its own scenes, choices, and branching outcomes.

-Every act registers its own scenes using our custom engine.

🧠 Sanity System

-Player sanity dynamically changes based on actions.

-Sanity affects dialogues, outcomes, and available paths.

-Background sanity “twitch animations” through a separate thread (Lapati).

🎒 Inventory System

-Persistent items meaningful to the story (e.g., silver locket, child drawing, oil can, etc.)

-Items unlock specific choices and alternate endings.

📘 Custom Game Engine

Built using:

-Scene (base)

-DialogueScene

-ChoiceScene

-EffectChoiceScene

-Simple extensible API to create new narrative scenes.

🗂 SQLite Integration

Saves progress locally using:

-DBUtil.java

-SaveDAO.java

🧵 Multithreading

Lapati whisper thread runs in the background and affects player sanity over time.

🎯 Choice-Driven Narrative

Branches, multiple outcomes, hidden scenes, and story-driven puzzles.

📁 Project Structure
src/main/java/com/fractured/
│
├── acts/
│   ├── Act1Register.java
│   ├── Act2Register.java
│   ├── Act3Register.java
│   ├── Act4Register.java
│   ├── Act5Register.java
│   └── Act6Register.java
│
├── core/
│   ├── GameEngine.java
│   ├── Scene.java
│   ├── DialogueScene.java
│   ├── ChoiceScene.java
│   ├── EffectChoiceScene.java
│   ├── GameState.java
│   └── GameException.java
│
├── db/
│   ├── DBUtil.java
│   └── SaveDAO.java
│
├── model/
│   ├── Player.java
│   └── Inventory.java
│
├── threads/
│   └── Lapati.java
│
└── ui/
    └── SanityDisplayController.java

🚀 How to Run
Prerequisites

-Java 17+

-Maven installed

-SQLite (auto-created on runtime)

💾 Saving & Loading

The game automatically creates fractured.db to store:

-Player sanity

-Inventory items

-Last scene visited

Database utilities are managed via:

-DBUtil.java

-SaveDAO.java

🧑‍💻 Author

Akarshit Yadav
Computer Science Engineering Student
Passionate about storytelling, game design, and backend development.
