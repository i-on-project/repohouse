package com.isel.leic.ps.ion_classcode.infra

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.http.HttpMethod
import java.net.URI

class SirenTests {
    // given: model classes
    data class UserModel(
        val name: String,
    )

    data class TeamModel(
        val teamId: Int,
    )

    @Test
    fun `can produce siren representation`() {
        // and: link relations
        val self = LinkRelation("self")
        val user = LinkRelation("https://example.com/rels/user")

        // and: a Jackson mapper
        val mapper = ObjectMapper().apply {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }

        // when: producing a Siren model
        val sirenModel = siren(
            TeamModel(
                teamId = 1,
            ),
        ) {
            clazz(value = "team")
            link(href = URI("https://example.com/teams/1"), rel = self)
            entity(UserModel(name = "user1"), rel = user) {
                link(URI("https://example.com/users/1"), rel = self)
            }
            entity(UserModel(name = "user2"), rel = user) {
                link(URI("https://example.com/users/2"), rel = self)
            }
            action(
                name = "remove user",
                URI("https://example.com/teams/1/remove/1"),
                method = HttpMethod.POST,
                type = "application/json",
            ) {
                textField(name = "reason")
            }
        }

        // and: serializing it to JSON
        val jsonString = mapper.writeValueAsString(sirenModel.body)

        // then: the serialization is the expected one
        val expected = """
            {
                "class":["team"],
                "properties": {
                    "teamId": 1
                },
                "entities":[
                    {
                        "rel": ["https://example.com/rels/user"],
                        "properties": {
                            "name": "user1"
                        },
                        "links": [
                            {"rel": ["self"], "href": "https://example.com/users/1", needAuthentication: false}
                        ]
                    },
                    {
                        "rel": ["https://example.com/rels/user"],
                        "properties": {
                            "name": "user2"
                        },
                        "links": [
                            {"rel": ["self"], "href": "https://example.com/users/2", needAuthentication: false}
                        ]
                    }
                ],
                "links": [
                    {"rel": ["self"], "href": "https://example.com/teams/1", needAuthentication: false}
                ],
                "actions": [
                    {"name": "remove user", "href":"https://example.com/teams/1/remove/1", "method":"POST", 
                      "type": "application/json",
                      "fields": [
                        {"name":"reason", "type": "text"}
                    ]}
                ]
            }
        """.trimIndent()
        JSONAssert.assertEquals(expected, jsonString, true)
    }
}
