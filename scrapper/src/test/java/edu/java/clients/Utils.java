package edu.java.clients;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static String GITHUB_TEST_RESPONSE =
        """
            [
              {
                "id": 16822682753,
                "node_id": "PSH_kwLOGwQzH88AAAAD6rXEgQ",
                "before": "18234e40d98ea4e27561b6b4ebd14590fc330c39",
                "after": "604c5d02a880f690ba4e9221027721e42c7db2cd",
                "ref": "refs/heads/main",
                "timestamp": "2024-01-27T19:09:00Z",
                "activity_type": "push",
                "actor": {
                  "login": "Nypiaka",
                  "id": 98625721,
                  "node_id": "U_kgDOBeDouQ",
                  "avatar_url": "https://avatars.githubusercontent.com/u/98625721?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/Nypiaka",
                  "html_url": "https://github.com/Nypiaka",
                  "followers_url": "https://api.github.com/users/Nypiaka/followers",
                  "following_url": "https://api.github.com/users/Nypiaka/following{/other_user}",
                  "gists_url": "https://api.github.com/users/Nypiaka/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/Nypiaka/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/Nypiaka/subscriptions",
                  "organizations_url": "https://api.github.com/users/Nypiaka/orgs",
                  "repos_url": "https://api.github.com/users/Nypiaka/repos",
                  "events_url": "https://api.github.com/users/Nypiaka/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/Nypiaka/received_events",
                  "type": "User",
                  "site_admin": false
                }
              }
            ]
            """;
    public static String STACKOVERFLOW_TEST_RESPONSE = """
        {
           "items":[
              {
                 "owner":{
                    "account_id":93689,
                    "reputation":418053,
                    "user_id":256196,
                    "user_type":"moderator",
                    "accept_rate":77,
                    "profile_image":"https://www.gravatar.com/avatar/d84b558fd67be10d5a718fb94231909d?s=256&d=identicon&r=PG",
                    "display_name":"Bohemian",
                    "link":"https://stackoverflow.com/users/256196/bohemian"
                 },
                 "creation_date":1703116800,
                 "down_vote_count":0,
                 "up_vote_count":1,
                 "post_id":31109486,
                 "question_id":15250928,
                 "timeline_type":"vote_aggregate"
              }
           ],
           "has_more":true,
           "quota_max":300,
           "quota_remaining":118
        }
        """;
}
