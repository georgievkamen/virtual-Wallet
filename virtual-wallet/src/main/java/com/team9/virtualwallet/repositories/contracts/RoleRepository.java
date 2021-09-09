package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Role;

public interface RoleRepository extends BaseRepository<Role> {

    boolean isDuplicate(String name);
}
