package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;


@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findItemRequestByRequestor_IdOrderByCreatedDesc(long userId);
    List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(long userId, PageRequest pageRequest);

}

