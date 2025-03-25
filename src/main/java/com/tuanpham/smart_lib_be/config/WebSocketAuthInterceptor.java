package com.tuanpham.smart_lib_be.config;

import com.tuanpham.smart_lib_be.util.SecurityUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final SecurityUtil securityUtil;

    public WebSocketAuthInterceptor(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                throw new IllegalArgumentException("Missing Authorization Header");
            }

            String token = authHeaders.get(0).replace("Bearer ", "");
            // Xử lý xác thực token ở đây (ví dụ: decode JWT)
            if(!this.securityUtil.checkValidTokenB(token)) {
                throw new IllegalArgumentException("Invalid Token");
            }
            String userName = this.securityUtil.getUserNameFromToken(token);
            accessor.setUser(new UsernamePasswordAuthenticationToken(userName, null, List.of()));
        }

        return message;
    }
}
