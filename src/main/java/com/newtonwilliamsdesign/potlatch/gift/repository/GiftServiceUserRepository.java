package com.newtonwilliamsdesign.potlatch.gift.repository;

/***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************
        G I F T
        A Multi-user Web Application and Android Client Application
        for sharing of image gifts.

        Copyright (C) 2014 Newton Williams Design.

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU Affero General Public License as
        published by the Free Software Foundation, either version 3 of the
        License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU Affero General Public License for more details.

        You should have received a copy of the GNU Affero General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************/

import java.util.ArrayList;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.newtonwilliamsdesign.potlatch.gift.domain.GiftServiceUser;

@Repository
public interface GiftServiceUserRepository extends PagingAndSortingRepository<GiftServiceUser, Long>{
	
	public GiftServiceUser findByName(String name);
	public GiftServiceUser findByUsername(String username);
	public GiftServiceUser findByUsernameOrderByTouchedcountDesc(String username);
	public ArrayList<GiftServiceUser> findTop10ByTouchedcountGreaterThanOrderByTouchedcountDesc(int touchedcount);
}
