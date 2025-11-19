package repository;

import entity.Identifiable;

import java.io.*;
import java.util.*;

/**
 * The base class for all application repositories
 * It provides core CRUD (Create, Read, Update, Delete) and file persistence functionality
 * using Java serialization for any entity that is both {@link Serializable} and {@link Identifiable}.
 *
 * @param <T> The type of the entity managed by this repository, which must implement
 * {@link Serializable} and {@link Identifiable}.
 */

public class Repository<T extends Serializable & Identifiable> {

    /** The file path used for saving and loading the entities. */
    protected final String filepath;

    /** The in-memory collection of all entities managed by this repository. */
    protected List<T> entities;
    

    /**
     * Constructs a Repository, loads existing entities from the specified file,
     * or initializes an empty list if the file is not found.
     *
     * @param filepath The path to the file used for serialization storage.
     */
    protected Repository(String filepath) {
        this.filepath = filepath;
        this.entities = loadFromFile();
    }

    /**
     * Loads the list of entities from the serialization file specified by {@code filepath}.
     * If the file does not exist, an empty list is returned.
     *
     * @return The list of entities loaded from the file, or an empty list upon failure or file absence.
     */
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

    /**
     * Saves the current in-memory list of entities to the file specified by {@code filepath}.
     * This method is typically called after any modification to the {@code entities} list.
     */
    protected void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(entities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves an entity (either inserting a new one or updating an existing one).
     * The method checks if an entity with the same ID already exists. If so, it updates it;
     * otherwise, it adds the new entity to the list and persists the changes.
     *
     * @param entity The entity to be saved or updated.
     */
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

    /**
     * Retrieves an entity by its unique ID.
     *
     * @param id The unique ID string of the entity to find.
     * @return The found entity, or {@code null} if no entity with the given ID exists.
     */
    public T findById(String id) {
        return entities.stream()
                .filter(entity -> entity.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a specified entity from the repository and persists the change.
     *
     * @param entity The entity instance to be deleted.
     */
    public void delete(T entity) {
        entities.remove(entity);
        saveToFile();
    }


    /**
     * Retrieves all entities currently managed by the repository.
     *
     * @return A new {@code ArrayList} containing all entities.
     */
    public ArrayList<T> findAll() {
        return new ArrayList<>(entities);
    }


    /**
     * Computes the maximum numeric part of entity IDs that start with a given prefix.
     * This is typically used by subclasses to determine the next available ID for new entities.
     *
     * @param prefix The ID prefix 
     * @return The highest numeric ID found, or 0 if no matching IDs are found.
     */
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