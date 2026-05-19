# Dynamo (NeoForge)

The neoforge implementation of Stator.

## How to use

For players, the mod can be downloaded on the Modrinth page: https://modrinth.com/mod/dynamo.

For developers, dynamo can be added to a project through the Modrinth maven. See the
[support page](https://support.modrinth.com/en/articles/8801191-modrinth-maven) for additional information.
The dynamo major and minor versions must match the stator major and minor versions, but the patch version may vary.

| Stator   | Dynamo |
|----------|--------|
| >= 0.1.1 | 0.1.0  |

The correct version should be used in the `implementation` block of the `build.gradle` file.

```groovy
dependencies {
    implementation "maven.modrinth:dynamo:${dynamo_version}-neoforge"
}
```

## License

This project is licensed under the MIT License.