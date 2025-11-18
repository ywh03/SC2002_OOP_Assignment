package repository;

import entity.Identifiable;

import java.io.*;
import java.util.*;

public abstract class Repository<T extends Serializable & Identifiable> {
    protected final String filepath;
    protected List<T> entities;

    protected Repository(String filepath) {
        this.filepath = filepath;
        this.entities = loadFromFile();
    }

    @SuppressWarnings("unchecked")
    protected List<T> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            return (List<T>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    protected void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(entities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(T entity) {
        String id = entity.getId();

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                entities.set(i, entity);
                saveToFile();
                return;
            }
        }

        entities.add(entity);
        saveToFile();
    }

    public T findById(String id) {
        return entities.stream()
                .filter(entity -> entity.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void delete(T entity) {
        entities.remove(entity);
        saveToFile();
    }

    public List<T> findAll() {
        return new ArrayList<>(entities);
    }

    protected int computeMaxNumericId(String prefix) {
        return entities.stream()
                .map(Identifiable::getId)
                .filter(id -> id.startsWith(prefix))
                .map(id -> id.substring(prefix.length()))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
    }

}