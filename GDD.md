# 🔪 GDD: Stu Must Kill

## 1. Overview
* **Genre:** Top-down Extraction Shooter / Puzzle
* **Engine:** LibGDX (Java)
* **Target Audience:** Core gamers, roguelite fans.
* **Elevator Pitch:** A 2D top-down extraction shooter built in LibGDX where the player navigates intense, close-quarters combat arenas. The core twist lies in its real-time grid-based inventory system (inspired by RE4), forcing players to make split-second spatial logic decisions to loot irregularly shaped weapons and gear before extracting or losing it all.

## 2. Core Mechanics (The "Verbs")
* **Shoot & Move:** Fast-paced, twin-stick style combat (WASD to move, Mouse to aim and shoot). Claustrophobic and highly lethal.
* **Loot & Tetris:** Real-time inventory management. The game does *not* pause when looting. Players must drag, drop, and rotate items (Tetris-style) to fit them into a limited grid while dodging threats.
* **Extract:** Reach the designated exit zone (e.g., an elevator) and survive a 5-second lockdown timer to keep the loot. Dying means losing everything in the grid.

## 3. The Vertical Slice (Scope)
* **Goal:** A fully playable 5-minute loop to showcase the technical architecture.
* **Content:**
    * 1 Map (The Bunker / Office Floor).
    * 1 Player character (Stu).
    * 2 Enemy types (Melee rusher, Ranged shooter).
    * 5 Lootable items (e.g., Pistol [2x1], Shotgun [3x1], Medkit [1x1], Ammo Box [1x1], Body Armor [2x2]).
    * 1 Extraction point.

## 4. Art & Vibe
* **Visual Style:** 2D top-down perspective. Dark, gritty pixel art (office-gone-wrong aesthetic). Dim lighting with flashlight mechanics.
* **Audio:** Tense ambient noise interrupted by punchy, loud gunshots and a fast-paced synthwave track during extraction.