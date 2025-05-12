# Matchle

Matchle is a Java-based word filtering and scoring engine inspired by pattern-matching games like Wordle. It uses n-gram structures to compare guesses against a corpus of valid answers, supporting logical filtering and scoring based on match quality. The project includes clean architectural design, defensive programming practices, unit testing, and code coverage analysis.

## Features

- NGram-based tokenization for string comparison
- Logical filters with combinable predicates
- Scoring system for evaluating guess quality
- Defensive programming and custom exception handling
- Comprehensive unit tests using JUnit 4
- Build automation with Apache Ant
- Code coverage reporting via JaCoCo
- JavaDoc documentation generation

## Technologies Used

- Java
- JUnit 4
- Apache Ant
- JaCoCo
- IntelliJ IDEA

## Build and Run Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/matchle.git
   cd matchle
   ```

2. Build the project:
   ```bash
   ant clean
   ant build
   ```

3. Compile the test file (if needed):
   ```bash
   javac -cp "build;lib/junit-4.13.jar;lib/hamcrest-core-2.2.jar" -d build test/matchle/MatchleTest.java
   ```

4. Run tests:
   ```bash
   ant test
   ```

5. Generate test and coverage reports:
   ```bash
   ant report
   ```

6. View results:
   - JavaDocs: `doc/index.html`
   - Coverage Report: `report/jacoco/html/index.html`
   - JUnit Report: `report/junit/html/index.html` (if applicable)

## Project Structure

```
matchle/
├── src/                # Source code (Corpus, NGram, Filter, etc.)
├── test/               # Unit tests (MatchleTest.java)
├── lib/                # Dependencies (JUnit, JaCoCo)
├── build/              # Compiled class files
├── report/             # Test and coverage reports
├── doc/                # JavaDoc output
├── build.xml           # Ant build script
└── README.md           # Project documentation
```

## License

This project was created for CWRU EECS 293 as part of a software craftsmanship course. All code is provided for academic and learning purposes.

## Contact

Evan Holtzmeier  
GitHub: [https://github.com/yourusername](https://github.com/evan-holtzmeier)  
