package com.ftalk.samsu.service;

import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;
import java.util.Set;

public interface GroupService {

	List<Group> getAllGroups();

	Group getGroupById(Integer id);

	Group getGroupByName(String name);

	Group updateGroup(GroupRequest group, Integer id);

	GroupImportMemberResponse addMemberToExistGroup(GroupRequest group, Integer id);

	Set<User> getGroupMembersById(Integer id);

	Group addGroup(GroupRequest group, UserPrincipal userPrincipal);

	ApiResponse deleteGroup(Integer groupID);

}
