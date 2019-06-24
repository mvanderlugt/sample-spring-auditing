/*
 * Copyright 2019 Mark Vander Lugt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package us.vanderlugt.sample.audit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
class UpdateUserAccount {
    @Size(min = 1, max = 100)
    private String username;
    @Size(min = 1, max = 100)
    private String password;
    @Size(min = 1, max = 50)
    private String firstName;
    @Size(min = 1, max = 100)
    private String lastName;
    @Email
    private String email;
}
