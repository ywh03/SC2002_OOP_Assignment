package repository;

import java.io.*;
import java.util.*;

public abstract class Repository<T extends Serializable> {
    protected final String filepath;
    protected List<T> entities;

    protected Repository(String filepath) {
        this.filepath = filepath;
        this.entities = loadFromFile();
    }

    @SuppressWarnings("")
    protected List<T> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            return (List<T>)ois.readObject();
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

    protected abstract String getId(T entity);

    public void save(T entity) {
        String id = getId(entity);

        for (int i = 0; i < entities.size(); i++) {
            if (getId(entities.get(i)).equals(id)) {
                entities.set(i, entity);
                saveToFile();
                return;
            }
        }

        entities.add(entity);
        saveToFile();
    }

    public void delete(T entity) {
        entities.remove(entity);
        saveToFile();
    }

    public ArrayList<T> getAll() {
        return new ArrayList<>(entities); // added this to return a copy of all objects,, check if getall = load
    }

}
