# Shuffly — Team 19

Shuffly is a JavaFX desktop music application designed to give users more control over how their Spotify music is shuffled and played.

Instead of relying only on a standard random shuffle, Shuffly provides multiple shuffle modes that change how songs are ordered. The application integrates with Spotify to access the user's music library and playlists, search music, manage a playback queue, and control Spotify playback.

## Team Members

| Role | Team Member | GitHub Username |
| --- | --- | --- |
| Project Manager | Alana Edwards | `alanaedwards1204` |
| Communications Lead | Jakobe Humphrey | `Jakobe6625` |
| Git Master | Wajid Zahid | `wajiddy` |
| Design Lead | Brad Fenasci | `bradfenasci` |
| Quality Assurance Tester | Asijah Wright | `asijahDenise` |

## Features

- Spotify authentication and integration
- Access to Spotify liked songs and playlists
- Music search
- Playlist and song display
- Playback controls
- Playback queue management
- Multiple custom shuffle modes
- Artist-spacing shuffle behavior
- Recent-song avoidance
- Mood-based shuffle development
- JavaFX desktop user interface

## Shuffle Modes

Shuffly provides several selectable shuffle modes:

- **Mood-based** — organizes music using mood-related song and artist information.
- **Truly Random** — randomly orders the selected music.
- **Anti-Popularity** — intended to reduce the priority of highly popular tracks.
- **Artist Spacing** — attempts to prevent songs from the same artist from appearing too close together.
- **Discovery** — intended to encourage greater variety and reduce repeated listening.

Some shuffle strategies may continue to be refined as development progresses.

## Platforms Tested

The current development build has been verified on:

- **Windows 11**

Other desktop operating systems capable of running Java 17 and JavaFX may work, but they should not be considered officially tested until verified by the team.

## Important Project Links

- **Kanban Board:** [Team 19 Kanban Board](https://github.com/orgs/CSC-3380-Fall-2026/projects/11)
- **Designs:** TODO — add direct design/Figma link
- **Code Standards / Style Guide:** TODO — add direct code standards link
- **Repository:** Team 19 GitHub repository

## Technology Stack

Shuffly is built with:

- **Java 17**
- **JavaFX 21.0.4**
- **Apache Maven**
- **Spotify Web API**
- **JSON in Java (`org.json`)**
- **JUnit 5**

## Dependencies

The Maven project currently uses the following dependencies and plugins.

### Application Dependencies

| Dependency | Version |
| --- | --- |
| Java | 17 |
| JavaFX Controls | 21.0.4 |
| JavaFX FXML | 21.0.4 |
| org.json | 20260814 |
| JUnit Jupiter | 5.10.2 |

### Maven Plugins

| Plugin | Version |
| --- | --- |
| Maven Compiler Plugin | 3.13.0 |
| JavaFX Maven Plugin | 0.0.8 |

The application's configured main class is:

```text
com.shuffly.ShufflyApp
```

## Prerequisites

Before running Shuffly from source, install:

1. **Java JDK 17**
2. **Apache Maven**

Confirm that both are available from the terminal:

```sh
java -version
mvn -version
```

Java should report version 17.

Maven must be available on the system PATH if the project is being launched from a terminal.

## Spotify Configuration

Shuffly uses the Spotify Web API for authentication, library access, playlists, and playback controls.

The Spotify configuration is located at:

```text
Shuffly/src/main/java/com/shuffly/spotify/SpotifyConfig.java
```

The configured redirect URI is:

```text
http://127.0.0.1:8888/callback
```

A valid Spotify Developer application must be configured to use the same redirect URI.

Spotify credentials are required for authentication. Client secrets should not be exposed in public repositories or documentation.

For playback controls to function, the user must also have an active Spotify playback device/session.

## Running Shuffly From Source

From the root of the Team 19 repository, enter the Maven project directory:

```sh
cd Shuffly
```

Maven will automatically download the JavaFX, JSON, JUnit, and other Maven-managed dependencies defined in `pom.xml`.

Compile the project with:

```sh
mvn clean compile
```

Launch Shuffly with:

```sh
mvn javafx:run
```

The JavaFX Maven plugin launches:

```text
com.shuffly.ShufflyApp
```

## Running Tests

From the `Shuffly` directory, run:

```sh
mvn test
```

This runs the project's JUnit test suite.

## Creating a Build

To clean and package the application:

```sh
mvn clean package
```

The generated Maven build files are placed in:

```text
Shuffly/target/
```

The `target` directory contains generated build output and should not be treated as source code.

## Development Workflow

Development work should be completed on task or feature branches created from `development`.

Changes should be submitted through pull requests back into `development` for review and integration.

Once the development build is stable and ready for release, the tested version can be merged from `development` into `main`.

The `main` branch should contain the stable version used for final grading and demonstration.

## Project Structure

```text
Team-19/
├── README.md
└── Shuffly/
    ├── pom.xml
    └── src/
        ├── main/
        │   └── java/
        │       └── com/
        │           └── shuffly/
        └── test/
```

The application source code is located under:

```text
Shuffly/src/main/java/com/shuffly/
```

Shuffle implementations are located under:

```text
Shuffly/src/main/java/com/shuffly/shuffle/
```

JavaFX pages and interface components are located under the application's UI packages.

## Notes for Grading

For the most reliable setup:

1. Use **Java 17**.
2. Ensure **Apache Maven** is installed and available from the command line.
3. Enter the `Shuffly` directory.
4. Run `mvn clean compile`.
5. Run `mvn javafx:run`.
6. Complete Spotify authentication when prompted.
7. Ensure an active Spotify playback device is available when testing playback functionality.
