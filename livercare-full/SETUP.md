# LiverCare — Fix Build Errors & First Run

## The Error You Saw

```
maven-compiler-plugin:3.11.0 ... TypeTag :: UNKNOWN
```

This means either:
1. **You're running the OLD zip** — the new zip fixes this by pinning compiler to 3.13.0
2. **IntelliJ hasn't reloaded the pom** — see Step 2 below
3. **Your JDK is mismatched** — see Step 1 below

---

## Step 1 — Verify JDK 17+ is active

Open a terminal (outside IntelliJ) and run:

```bash
java -version
mvn -version
```

Both must show Java 17 or higher.  
If `mvn -version` shows Java 11, fix it:

**Windows:**
```
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

**Mac/Linux:**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Or in IntelliJ: **File → Project Structure → Project SDK** → set to JDK 17+

---

## Step 2 — Reload Maven in IntelliJ

After extracting the new ZIP:

1. Open IntelliJ → right-click `pom.xml` → **"Add as Maven Project"**  
   (or if already a Maven project: right-click `pom.xml` → **"Maven" → "Reload project"**)
2. Wait for indexing to finish
3. Run: **Maven panel → livercare-backend → Lifecycle → install**

---

## Step 3 — Build from terminal (fastest verification)

```bash
cd livercare-full/backend
mvn clean compile -e
```

If it still fails, run with full debug:
```bash
mvn clean compile -X 2>&1 | head -60
```

---

## Step 4 — Run the full stack

```bash
cd livercare-full
cp .env.example .env
# Edit .env — add your AWS credentials (for Bedrock)

docker compose up --build
```

| Service | URL |
|---------|-----|
| React Dashboard | http://localhost:3000 |
| Spring Boot API | http://localhost:8080/api/swagger-ui.html |
| AI Drift Engine | http://localhost:8000/docs |

Default credentials: `admin` / `livercare2024`

---

## What changed in this ZIP vs the previous one

| File | Fix |
|------|-----|
| `pom.xml` | Compiler pinned to `3.13.0` (was `3.11.0` via Spring Boot parent) |
| All `.java` files | **Lombok completely removed** — no more `@RequiredArgsConstructor`, `@Data`, `@Builder`, `@Getter`, `@Setter` |
| All classes | Explicit Java constructors and getters/setters — compiles on any JDK 17 toolchain |
