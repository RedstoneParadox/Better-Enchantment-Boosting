[![curseforge](https://img.shields.io/badge/-CurseForge-gray?style=for-the-badge&logo=curseforge&labelColor=orange)](https://www.curseforge.com/minecraft/mc-mods/better-enchantment-boosting)
[![modrinth](https://img.shields.io/badge/-modrinth-gray?style=for-the-badge&labelColor=green&labelWidth=15&logo=appveyor&logoColor=white)](https://modrinth.com/mod/better-enchantment-boosting)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/RedstoneParadox/Better-Enchantment-Boosting?logo=github&style=for-the-badge)
![Discord](https://img.shields.io/discord/686427164962455560?label=Discord&style=for-the-badge)

# Better Enchantment Boosting

A Minecraft mod that allows bookshelves to boost the Enchanting Table from farther away.

### Adding Better Enchantment Boosting To Your Project

`build.gradle`:

```gradle
maven {
    name "redstoneparadoxRepositoryReleases"
    url "https://maven.redstoneparadox.xyz/releases"
}

dependencies {
    modImplementation "io.github.redstoneparadox:Better-Enchantment-Boosting:<version>"
}
```

`build.gradle.kts`:

```kotlin
maven {
    name = "redstoneparadoxRepositoryReleases"
    url = uri("https://maven.redstoneparadox.xyz/releases")
}

dependencies {
	modImplementation("io.github.redstoneparadoxBetter-Enchantment-Boosting:<version>")
}
```

Note that versions before 1.4.1 aren't available on my maven.
