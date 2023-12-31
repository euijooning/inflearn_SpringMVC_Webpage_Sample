package hello.itemservice.web.from;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {

    private final ItemRepository itemRepository;

    /**
     * 이렇게 하면 이 컨트롤러를 호출할 때는
     * 항상 얘가 ModelAttribute 에 자동으로 addAttribute 되어가지고
     * Model에 무조건 담긴다.
     * 그래서 일일이 메서드에 추가했던 코드를 다시 다 지울 수 있게 된다.
     * @return
     */
    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }


    // @ModelAttribute 어노테이션이 적용된 메서드
    // 이 메서드는 "itemTypes"라는 이름으로 모델에 데이터를 추가한다.
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        // ItemType 열거형(enum)의 모든 값을 배열로 반환.
        // ItemType.values() 메서드는 enum에서 정의한 모든 열거 상수를 배열로 제공.
        return ItemType.values();
    }


    /**
     * 모델에 배송 코드 목록을 추가하는 메서드.
     *
     * @return 배송 코드 목록을 포함하는 List<DeliveryCode>
     */
    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        // 배송 코드 목록을 생성.
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));

        // 생성된 배송 코드 목록을 반환.
        return deliveryCodes;
    }


    @GetMapping
    public String items(Model model) {
        // 상품 목록을 조회
        List<Item> items = itemRepository.findAll();
        // 조회한 상품 목록을 모델에 추가
        model.addAttribute("items", items);
        // "form/items" 뷰 템플릿을 호출하여 상품 목록을 화면에 표시
        return "form/items";
    }


    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        // 경로 변수로부터 받은 itemId를 사용하여 상품을 조회.
        Item item = itemRepository.findById(itemId);
        // 조회한 상품을 모델에 추가. 이후 뷰 템플릿에서 사용할 수 있다.
        model.addAttribute("item", item);
        // "form/item" 뷰 템플릿을 호출하여 상품 상세 정보를 화면에 표시.

        return "form/item";
    }


    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());

        return "form/addForm";
    }


    /**
     * 상품을 추가하는 메서드.
     *
     * @param item               추가할 상품 정보를 나타내는 객체
     * @param redirectAttributes Spring MVC 리다이렉트 시 데이터를 전달하기 위한 객체
     * @return 상품 추가 후 해당 상품 상세 정보 페이지로 리다이렉트
     */
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {

        log.info("item.open={}", item.getOpen());
        log.info("item.regions={}", item.getRegions());
        log.info("item.itemType={}", item.getItemType());

        // 상품 정보를 데이터베이스에 저장하고 저장된 상품 정보를 반환.
        Item savedItem = itemRepository.save(item);

        // 리다이렉트 시 URL에 파라미터를 추가하기 위해 RedirectAttributes를 사용.
        // "itemId" 파라미터에 저장된 상품의 ID를 추가.
        redirectAttributes.addAttribute("itemId", savedItem.getId());

        // "status" 파라미터를 추가하고 값을 true로 설정. (예: 성공적으로 상품을 추가한 상태를 나타냄)
        redirectAttributes.addAttribute("status", true);

        // 상품 추가 후 해당 상품의 상세 정보 페이지로 리다이렉트.
        return "redirect:/form/items/{itemId}";
    }



    /**
     * 지정된 ID를 가진 아이템의 수정 폼을 표시한다.
     *
     * @param itemId 수정할 아이템의 ID
     * @param model 뷰에 데이터를 담기 위한 모델
     * @return 수정 폼 뷰의 이름
     */
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        // 아이템 레포지토리에서 아이템을 ID를 사용하여 검색.
        Item item = itemRepository.findById(itemId);

        // 검색한 아이템을 모델에 추가하여 뷰에서 표시할 수 있도록 함.
        model.addAttribute("item", item);


        // 수정 폼 뷰의 이름을 반환.
        return "form/editForm";
    }


    /**
     * 상품을 수정하는 메서드.
     *
     * @param itemId 수정할 상품의 ID
     * @param item   수정된 상품 정보를 나타내는 객체
     * @return 상품 수정 후 해당 상품 상세 정보 페이지로 리다이렉트
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        // 상품 수정 메서드를 호출하여 상품 정보를 업데이트 (itemRepository.update 메서드 사용)
        itemRepository.update(itemId, item);

        // 수정된 상품 정보를 포함하는 해당 상품의 상세 정보 페이지로 리다이렉트.
        return "redirect:/form/items/{itemId}";
    }
}
