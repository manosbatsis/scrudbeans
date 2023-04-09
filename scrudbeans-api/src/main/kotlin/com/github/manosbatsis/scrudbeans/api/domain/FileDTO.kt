/**
 *
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.api.domain

import java.io.File

data class FileDTO(
    var contentLength: Long = 0,
    var contentType: String? = null,
    var `in`: File? = null,
    var path: String? = null,
    var tmpFile: File? = null,

) {
    class Builder {
        private var contentLength: Long = 0
        private var contentType: String? = null
        private var `in`: File? = null
        private var path: String? = null
        private var tmpFile: File? = null
        fun contentLength(contentLength: Long): Builder {
            this.contentLength = contentLength
            return this
        }

        fun contentType(contentType: String?): Builder {
            this.contentType = contentType
            return this
        }

        fun `in`(`in`: File?): Builder {
            this.`in` = `in`
            return this
        }

        fun path(path: String?): Builder {
            this.path = path
            return this
        }

        fun tmpFile(tmpFile: File?): Builder {
            this.tmpFile = tmpFile
            return this
        }

        fun build(): FileDTO {
            return FileDTO(
                contentLength = contentLength,
                contentType = contentType,
                `in` = `in`,
                path = path,
                tmpFile = tmpFile,
            )
        }
    }
}
