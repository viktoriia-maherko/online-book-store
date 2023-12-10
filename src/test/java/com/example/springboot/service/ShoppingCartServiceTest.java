package com.example.springboot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.springboot.dto.cartitem.CartItemDto;
import com.example.springboot.dto.cartitem.CreateCartItemRequestDto;
import com.example.springboot.dto.cartitem.UpdateCartItemRequestDto;
import com.example.springboot.dto.shoppingcart.ShoppingCartDto;
import com.example.springboot.mapper.CartItemMapper;
import com.example.springboot.mapper.ShoppingCartMapper;
import com.example.springboot.model.Book;
import com.example.springboot.model.CartItem;
import com.example.springboot.model.Role;
import com.example.springboot.model.ShoppingCart;
import com.example.springboot.model.User;
import com.example.springboot.repository.cartitem.CartItemRepository;
import com.example.springboot.repository.shoppingcart.ShoppingCartRepository;
import com.example.springboot.service.impl.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Verify the correct shoppingCartDto was returned by valid userId")
    void getById_WithValidUserId_ShouldReturnShoppingCartDto() {
        Book book = createBook();
        User user = createUser();
        CartItem cartItem = createCartItem(book);
        CartItemDto cartItemDto = createCartItemDto();
        ShoppingCart shoppingCart = createShoppingCart(user, cartItem);
        ShoppingCartDto expected = createShoppingCartDto(cartItemDto);
        when(shoppingCartRepository
                .findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getShoppingCartByUserId(user.getId());
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify the correct book was added to CartItemDto")
    void addBookToTheShoppingCart_ValidCreateCartItemRequestDto_ReturnsCartItemDto() {
        User user = createUser();
        Book book = createBook();
        CreateCartItemRequestDto cartItemRequestDto = createCartItemRequestDto(book);
        CartItem cartItem = createCartItem(book);
        CartItemDto expected = createCartItemDto();
        Long userId = user.getId();
        ShoppingCart shoppingCart = createShoppingCart(user, cartItem);

        when(cartItemMapper.toEntity(cartItemRequestDto)).thenReturn(cartItem);
        when(shoppingCartRepository.findShoppingCartByUserId(userId))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        CartItemDto actual = shoppingCartService
                .addBookToTheShoppingCart(userId, cartItemRequestDto);
        assertEquals(expected, actual);

        verify(cartItemMapper, times(1)).toEntity(cartItemRequestDto);
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("Verify the cartItemDto was updated correctly "
            + "by valid cartItemId and cartItemRequestDto")
    void updateByIdAndCartItemRequestDto_ShouldReturnCartItemDto() {
        Book book = createBook();
        UpdateCartItemRequestDto updateCartItemRequestDto = new UpdateCartItemRequestDto();
        updateCartItemRequestDto.setQuantity(200);
        CartItem cartItem = createCartItem(book);
        Long cartItemId = cartItem.getId();
        CartItemDto expected = createCartItemDto();
        expected.setQuantity(200);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        CartItemDto actual = shoppingCartService
                .updateBookInTheShoppingCart(cartItemId, updateCartItemRequestDto);
        assertEquals(expected, actual);

        verify(cartItemRepository, times(1)).findById(cartItemId);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("Verify method delete was invoked")
    void removeBookFromTheShoppingCart_ValidCartItemId_Success() {
        Book book = createBook();
        CartItem cartItem = createCartItem(book);
        Long cartItemId = cartItem.getId();

        shoppingCartService.removeBookFromTheShoppingCart(cartItemId);

        verify(cartItemRepository, times(1)).deleteById(cartItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    private ShoppingCart createShoppingCart(User user, CartItem cartItem) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(cartItem));
        return shoppingCart;
    }

    private CartItemDto createCartItemDto() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setQuantity(100);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Harry Potter");
        return cartItemDto;
    }

    private CreateCartItemRequestDto createCartItemRequestDto(Book book) {
        CreateCartItemRequestDto cartItemRequestDto = new CreateCartItemRequestDto();
        cartItemRequestDto.setQuantity(100);
        cartItemRequestDto.setBookId(book.getId());
        return cartItemRequestDto;
    }

    private ShoppingCartDto createShoppingCartDto(CartItemDto cartItemDto) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setUserId(1L);
        shoppingCartDto.setCartItems(Set.of(cartItemDto));
        return shoppingCartDto;
    }

    private CartItem createCartItem(Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(100);
        cartItem.setBook(book);
        return cartItem;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@gmail.com");
        user.setPassword("qwerty12345");
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setShippingAddress("USA");
        Role role = new Role();
        role.setRoleName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setDeleted(false);
        return user;
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setAuthor("Rouling");
        book.setIsbn("12345678");
        book.setDescription("Story about magic world");
        book.setPrice(BigDecimal.valueOf(100));
        return book;
    }
}
