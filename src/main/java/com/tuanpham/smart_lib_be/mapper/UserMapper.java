package com.tuanpham.smart_lib_be.mapper;
import com.tuanpham.smart_lib_be.domain.Response.ResCreateUserDTO;
import com.tuanpham.smart_lib_be.domain.Response.ResUserDTO;
import com.tuanpham.smart_lib_be.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(User userCreateReq);

    ResCreateUserDTO toResCreateUserDTO(User user);
    @Mapping(target = "cardRead", expression = "java(user.getCardRead() != null ? user.getCardRead() : null)")
    ResUserDTO toResUserDTO(User user);
}
