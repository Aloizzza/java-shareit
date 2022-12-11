package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequestorIdOrderByCreatedDesc(PageRequest pageRequest, long userId);

    List<Request> findAllByRequestorIdNotOrderByCreatedDesc(PageRequest pageRequest, long userId);
}
