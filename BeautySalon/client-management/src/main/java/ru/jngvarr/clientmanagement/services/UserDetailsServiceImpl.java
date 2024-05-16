package ru.jngvarr.clientmanagement.services;

import dao.entities.people.Employee;
import dao.entities.people.User;
import feign_clients.StaffFeignClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jngvarr.clientmanagement.repositories.UserRepository;

import java.util.Objects;

@Data
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffFeignClient staffFeignClient;

    public boolean isUserIsManager(User user) { //TODO дописать метод
        return user
        if (isUserIsManager(this)) auths.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
    }

    public boolean getClientByUserContact(User user) {
        Employee employee = staffFeignClient.getEmployeeByPhone(user.getContact());
        return employee != null && employee.getContact().equals(user.getContact());
    }

    public UserDetails getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(isUserIsManager(user))user.get
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
