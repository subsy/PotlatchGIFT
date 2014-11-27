package com.newtonwilliamsdesign.potlatch.gift.mvc;

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

public interface ControllerPaths {

	public static final String TITLE_PARAMETER = "title";

	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String DATA_PARAMETER = "image";

	public static final String ID_PARAMETER = "id";

	// The path where we expect the UserSvc to live
	public static final String USER_SVC_PATH = "/user";
	
	// The path where we expect the VideoSvc to live
	public static final String GIFT_SVC_PATH = "/gift";
	
	public static final String COUNT_SVC_PATH = "/count";
	
	// The path for the Gift (image) data
	public static final String GIFT_IMG_PATH = GIFT_SVC_PATH + "/{id}/image";
	
	// The path for the Gift (image) thumbnail data
	public static final String GIFT_THUMB_PATH = GIFT_SVC_PATH + "/{id}/thumb";

	// The path to search videos by title
	public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByTitle";
}
