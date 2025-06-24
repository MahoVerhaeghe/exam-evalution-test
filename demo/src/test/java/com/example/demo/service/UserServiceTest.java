package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.ObjectNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testCreateUser_success() {
        User user = getUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        assertNotNull(userService.createUser(user));
    }

    @Test
    public void testCreateUser_duplicateEmail() {
        User user = getUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user));
    }

    @Test
    public void testGetUserById_success() {
        User user = getUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user.getEmail(), userService.getUserById(1L).getEmail());
    }

    @Test
    public void testGetUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testUpdateUser_success() {
        User user = getUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        assertEquals(user.getEmail(), userService.updateUser(1L, user).getEmail());
    }

    @Test
    public void testUpdateUser_emailExists() {
        User user = getUser();
        User existing = getUser();
        existing.setEmail("another@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    public void testUpdateUser_notFound() {
        User user = getUser();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1L, user));
    }

    @Test
    public void testDeleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUser_notFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPassword("12345");
        return user;
    }
}
