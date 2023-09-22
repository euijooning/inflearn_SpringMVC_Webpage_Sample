package hello.itemservice.repository;

import hello.itemservice.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository // 인터페이스가 아니므로 붙인 듯. 안에 @Component 있음
public class ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>();
    // 실무 멀티 스레드 환경에서는 HashMap 을 쓰면 안 된다.
    // 사용하고 싶다면 currentHashMap 을 써야한다.
    private static long sequence = 0L;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item item) {
        Item findItem = findById(itemId);
        findItem.setItemName(item.getItemName());
        findItem.setPrice(item.getPrice());
        findItem.setQuantity(item.getQuantity());
    }

    public void clearStore() { // 테스트용 메서드
        store.clear();
    }

}