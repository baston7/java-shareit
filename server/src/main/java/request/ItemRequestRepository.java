package request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import request.model.ItemRequest;

import java.util.List;


@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findItemRequestByRequestorIdOrderByCreatedDesc(long userId);

    List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(long userId, PageRequest pageRequest);
}
