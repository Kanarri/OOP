package model;

import java.util.Objects;

/**
 * Кандидат в избирательном округе.
 * Идентичность определяется полем {@code id}:
 * два кандидата равны, если равны их {@code id}
 *
 * @param id   уникальный идентификатор кандидата
 * @param name отображаемое имя
 */
public record Candidate(String id, String name){
    public Candidate{
        if (id == null) {
            throw new IllegalArgumentException("id can't be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name can't be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Candidate other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }
}