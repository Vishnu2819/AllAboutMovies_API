# AllAboutMovies API

The REST backend for [AllAboutMovies](https://github.com/Vishnu2819/AllAboutMovies_UI), built with Java 21, Spring Boot 3 and MongoDB. It serves a movie catalog and lets users post reviews with ratings.

**Frontend:** https://vishnu2819.github.io/AllAboutMovies_UI/

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/movies?genre=&q=` | All movies, newest first. Optional genre filter and case-insensitive title search |
| `GET` | `/api/v1/movies/{imdbId}` | `{ movie, reviews, averageRating }` |
| `GET` | `/api/v1/movies/{imdbId}/reviews` | Reviews for a movie, newest first |
| `POST` | `/api/v1/movies/{imdbId}/reviews` | Create a review: `{ "author": "Sam", "rating": 1-5, "body": "..." }` → `201` with a `Location` header |
| `GET` | `/api/v1/genres` | Distinct genres, sorted |
| `GET` | `/actuator/health` | Health check |

Errors are returned as RFC 7807 problem details. Validation failures include a field-by-field `errors` map:

```json
{ "status": 400, "detail": "Request body failed validation", "errors": { "rating": "must be less than or equal to 5" } }
```

## Design

- **Layers:** controller → service → Spring Data repository. Movies and reviews are separate collections, joined by `imdbId` (unique index on movies, regular index on reviews).
- **Records:** Java records for documents and DTOs, and Bean Validation on input.
- **Seed data:** on first start with an empty database, `MovieSeeder` loads 14 films from `src/main/resources/movies.json` (metadata and artwork from TMDB). Set `SEED_ENABLED=false` to turn it off.
- **CORS:** allowed origins come from `CORS_ALLOWED_ORIGINS`; by default `http://localhost:3000` and the GitHub Pages frontend.
- **Testable time:** `ReviewService` takes a `Clock`, so tests can fix timestamps.

## Tests

```bash
./mvnw verify
```

- Unit tests for the services, with mocked repositories.
- `@WebMvcTest` tests for the controllers, covering status codes, JSON shape, validation errors, problem details and CORS.
- One full-stack integration test against an embedded MongoDB (Flapdoodle) that covers seeding, filtering, posting reviews and the rating average.

CI runs `./mvnw verify` on every push (`.github/workflows/ci.yml`).

## Run locally

With Docker:

```bash
docker compose up --build      # API on http://localhost:8080, MongoDB on 27017
```

Or with a local MongoDB:

```bash
export MONGODB_URI=mongodb://localhost:27017/allaboutmovies   # this is the default
./mvnw spring-boot:run
curl localhost:8080/api/v1/movies
```

| Variable | Default |
|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/allaboutmovies` |
| `PORT` | `8080` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,https://vishnu2819.github.io` |
| `SEED_ENABLED` | `true` |

## Deploying

GitHub Pages can only host static files, so this API needs a JVM host (Render, Railway, Fly.io, AWS App Runner, or ECS) plus a MongoDB, for example a free MongoDB Atlas cluster. The `Dockerfile` builds a small JRE image. After you deploy, set `REACT_APP_API_URL` on the frontend build to point at it. Until then, the frontend uses its bundled copy of the same catalog.
