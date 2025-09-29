package com.sobow.shopping.controllers.order;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.shopping.controllers.order.dto.OrderResponse;
import com.sobow.shopping.domain.order.Order;
import com.sobow.shopping.exceptions.CartEmptyException;
import com.sobow.shopping.mappers.order.OrderResponseMapper;
import com.sobow.shopping.services.order.OrderService;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTests {
    
    public static final String ORDER_PATH = "/api/users/me/orders";
    public static final String ORDER_PATH_BY_ID = "/api/users/me/orders/{orderId}";
    
    @MockitoBean
    private OrderService orderService;
    
    @MockitoBean
    private OrderResponseMapper orderResponseMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Test
    public void selfCreateOrder_should_Return201_when_CartNotEmpty() throws Exception {
        // Given
        Order order = fixtures.orderEntity();
        OrderResponse orderResponse = fixtures.orderResponse();
        
        when(orderService.selfCreateOrder()).thenReturn(order);
        when(orderResponseMapper.mapToDto(order)).thenReturn(orderResponse);
        
        // When & Then
        mockMvc.perform(post(ORDER_PATH))
               .andExpect(status().isCreated())
               .andExpect(header().exists(HttpHeaders.LOCATION))
               .andExpect(jsonPath("$.message").value("Created"))
               .andExpect(jsonPath("$.data").exists());
    }
    
    @Test
    public void selfCreateOrder_should_Return422_when_CartEmpty() throws Exception {
        // Given
        when(orderService.selfCreateOrder()).thenThrow(new CartEmptyException(fixtures.cartId()));
        
        // When & Then
        mockMvc.perform(post(ORDER_PATH))
               .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    void getAllOrders_should_Return200_when_OrdersExist() throws Exception {
        // Given
        
        Order order1 = fixtures.orderEntity();
        Order order2 = fixtures.orderEntity();
        
        OrderResponse response1 = fixtures.orderResponse();
        OrderResponse response2 = fixtures.orderResponse();
        
        when(orderService.selfFindAllWithItems()).thenReturn(List.of(order1, order2));
        when(orderResponseMapper.mapToDto(order1)).thenReturn(response1);
        when(orderResponseMapper.mapToDto(order2)).thenReturn(response2);
        
        // When & Then
        mockMvc.perform(get(ORDER_PATH))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.message").value("Found"))
               .andExpect(jsonPath("$.data").isArray())
               .andExpect(jsonPath("$.data.length()").value(2));
    }
    
    
    @Test
    void getOrder_should_Returns200_when_OrderExists() throws Exception {

        long orderId = fixtures.orderId();
        
        Order order = fixtures.orderEntity();
        OrderResponse response = fixtures.orderResponse();
        
        when(orderService.selfFindByIdWithItems(orderId)).thenReturn(order);
        when(orderResponseMapper.mapToDto(order)).thenReturn(response);
        
        mockMvc.perform(get(ORDER_PATH_BY_ID, orderId))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.message").value("Found"))
               .andExpect(jsonPath("$.data").exists());
    }
    
    @Test
    void getOrder_should_Return404_when_OrderNotFound() throws Exception {
        long orderId = fixtures.nonExistingId();
        
        when(orderService.selfFindByIdWithItems(orderId))
            .thenThrow(new EntityNotFoundException());
        
        mockMvc.perform(get(ORDER_PATH_BY_ID, orderId))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void getOrder_should_Return400_when_IdLessThanOne() throws Exception {
        long orderId = fixtures.invalidId();
        mockMvc.perform(get(ORDER_PATH_BY_ID, orderId))
               .andExpect(status().isBadRequest());
    }
}