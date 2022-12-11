package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where i.available = true and" +
            " (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "  or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text1, String text2);

    List<Item> findAllByOwnerIdOrderByIdDesc(long ownerId);

    List<Item> findItemsByRequestId(long requestId);

    @Query(" select i from Item i " +
            "where i.request is not null")
    List<Item> findAllWithNonNullRequest();
}