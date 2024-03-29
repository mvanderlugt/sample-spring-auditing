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

package us.vanderlugt.sample.audit.role;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import us.vanderlugt.sample.audit.common.MapperConfiguration;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(config = MapperConfiguration.class)
interface AccessRuleMapper {
    AccessRule create(NewAccessRule newRule);

    void apply(UpdateAccessRule update, @MappingTarget AccessRule role);
}
