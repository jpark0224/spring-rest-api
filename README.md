# 🏋️ Exercise Tracker API Reference

## Status

### 📄 Get a status

```bash
curl -v localhost:8080/status | json_pp
```

---

## Logs

### ➕ Create a log

```bash
curl -v -X POST localhost:8080/logs \
  -H 'Content-Type:application/json' \
  -d '{"name": "Legs"}' | json_pp
```

### 📄 Get all logs

```bash
curl -v localhost:8080/logs | json_pp
```

### 📄 Get a log

```bash
curl -v http://localhost:8080/logs/1 | json_pp
```

### ✅ Complete a log

```bash
curl -v -X PUT localhost:8080/logs/1/complete | json_pp
```

### ❌ Delete a log

```bash
curl -X DELETE localhost:8080/logs/1
```

---

## Exercise Templates (`/exercises`)

### ➕ Create a new exercise template

```bash
curl -v -X POST localhost:8080/exercises \
  -H 'Content-Type:application/json' \
  -d '{"name": "dumbbell lunge", "primaryMuscleGroup": "glutes"}' | json_pp
```

### 📄 Get all exercise templates

```bash
curl -v localhost:8080/exercises | json_pp
```

### 📄 Get an exercise template

```bash
curl -v localhost:8080/exercises/1 | json_pp
```

### 📝 Update an exercise template

```bash
curl -v -X PUT localhost:8080/exercises/1 \
  -H 'Content-Type:application/json' \
  -d '{"name": "machine seated leg press", "primaryMuscleGroup": "quads"}' | json_pp
```

### ❌ Delete an exercise template

```bash
curl -X DELETE localhost:8080/exercises/1
```

---

## 📦 Exercises in a Log (`/logs/{logId}/exercises`)

### ➕ Copy an exercise from template to a log

```bash
curl -v -X POST http://localhost:8080/logs/1/exercises/from-template/1 | json_pp
```

### 📄 Get all exercises in a log

```bash
curl -v http://localhost:8080/logs/1/exercises | json_pp
```

### 📄 Get an exercise in a log

```bash
curl -v http://localhost:8080/logs/1/exercises/1 | json_pp
```

### ❌ Delete an exercise from a log

```bash
curl -X DELETE http://localhost:8080/logs/1/exercises/1
```

---

## 🏋️ Sets (`/logs/{logId}/exercises/{exerciseCopyId}/sets`)

### ➕ Add a set to an exercise

```bash
curl -v -X POST localhost:8080/logs/1/exercises/1/sets \
  -H 'Content-Type:application/json' \
  -d '{"reps": 12, "weight": 10}' | json_pp
```

### 📄 Get all sets of an exercise

```bash
curl -v localhost:8080/logs/1/exercises/1/sets | json_pp
```

### 📄 Get a set

```bash
curl -v localhost:8080/logs/1/exercises/1/sets/1 | json_pp
```

### 📝 Update a set

```bash
curl -v -X PUT localhost:8080/logs/1/exercises/1/sets/1 \
  -H 'Content-Type:application/json' \
  -d '{"reps": "8", "weight": "12"}' | json_pp
```

### ❌ Delete a set

```bash
curl -X DELETE localhost:8080/logs/1/exercises/1/sets/1
```
