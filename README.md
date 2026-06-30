<div align="center">

# 🌌 SkinVault

A streamlined, modern client-side Fabric mod for managing, saving, and instantly switching between your favorite Minecraft skins.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-orange?style=for-the-badge&logo=fabric)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License](https://img.shields.io/github/license/simonov41coder/skinvault?style=for-the-badge)](LICENSE)

[![Release](https://img.shields.io/github/v/release/simonov41coder/skinvault?style=flat-square)](https://github.com/simonov41coder/skinvault/releases)
[![Build](https://img.shields.io/github/actions/workflow/status/simonov41coder/skinvault/build.yml?style=flat-square)](https://github.com/simonov41coder/skinvault/actions)
[![Stars](https://img.shields.io/github/stars/simonov41coder/skinvault?style=flat-square)](https://github.com/simonov41coder/skinvault/stargazers)
[![Issues](https://img.shields.io/github/issues/simonov41coder/skinvault?style=flat-square)](https://github.com/simonov41coder/skinvault/issues)

</div>

---

## ✨ Features

- **💾 Unlimited Skin Storage:** Save your favorite skin URLs locally with distinct names.
- **🔍 Real-Time Search:** Instantly filter through your list using the built-in search bar.
- **🔄 One-Click Application:** Instantly push skin update commands with a single click.
- **🛠️ Full CRUD Controls:** Effortlessly add, edit, or delete skin entries inside a smooth custom modal interface.
- **🕊️ Lightweight & Client-Side:** Purely client-side configuration. No server footprint required.

## ⚙️ Dependencies

SkinVault requires the following libraries to run:
* **[Fabric Loader](https://fabricmc.net/)**
* **[Fabric API](https://modrinth.com/mod/fabric-api)**
* **[owo-lib (by Wisp Forest)](https://modrinth.com/mod/owo-lib)** — *Powers the custom user interface layout.*

## 📦 Installation

1. Download and install the **Fabric Loader**.
2. Download **Fabric API** and **owo-lib** and drop them into your Minecraft installation's `mods` folder.
3. Download the latest version of **SkinVault** from the [Releases](https://github.com/simonov41coder/skinvault/releases) tab.
4. Put the SkinVault `.jar` file into your `mods` folder.
5. Launch the game!

## 🚀 Usage

* Open the Skin Vault interface via your configured hotkey *(default keybind can be set in Minecraft's Controls menu)*.
* Click the **`+` (Plus)** button to add a skin by entering its name and direct URL.
* Clicking on any skin name button will automatically run the background integration command:
  ```text
  /skin url <your-saved-url>

```
 * Use the **Edit** (pencil) or **Delete** (trash) icons to modify your library anytime. All changes autosave instantly.
## 🔨 Building From Source
If you prefer to compile the mod yourself, clone the repository and build via the Gradle wrapper:
```bash
git clone [https://github.com/simonov41coder/skinvault.git](https://github.com/simonov41coder/skinvault.git)
cd skinvault
./gradlew build

```
Once completed, your freshly compiled build will be generated in:
```text
build/libs/

```
## 📄 License
This project is open-source software licensed under the **Apache License 2.0**. See the LICENSE file for more information.
## 📈 Star History
Star History Chart
```
