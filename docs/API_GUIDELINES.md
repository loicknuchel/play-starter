# API GUIDELINES

## Sample

Here is what API may look like :

- `GET    /api/v1/users       controllers.Users.find(page: Int ?= 0, pageSize: Int ?= 20, q: Option[String], filter: Option[JsValue], sort: List[String], fields: List[String])` : retrieves a page of users
- `POST   /api/v1/users       controllers.Users.create` : (+ json user payload) creates a user an returns the full resource
- `GET    /api/v1/users/:id   controllers.Users.get(id: User.Id, fields: List[String])` : retrieves a specific user
- `PUT    /api/v1/users/:id   controllers.Users.fullUpdate(id: User.Id)` : (+ json user payload) fully update a user an returns the full resource
- `PATCH  /api/v1/users/:id   controllers.Users.update(id: User.Id)` : (+ json partial user payload) partially update a user (with fields presents) an returns the full resource
- `DELETE /api/v1/users/:id   controllers.Users.delete(id: User.Id)` : delete specified User

Good documentation is a must have and can be generated with [api blueprint](https://apiblueprint.org/)

## Global

- find parameters
    - q: full-text search
    - filter: matching results
    - sort: List of String with possible negatives (ex: sort=category,-created)
    - fields: list of fields that should be included
- auth via token
- rate limiting
- SSL
- CORS
- gzip
- Overriding HTTP method with POST + header "X-HTTP-Method-Override" with real verb
- json envelope : `{data: {}, meta: {}}`
- json error :
```
{
    code: 1234, // unique error code lookable in the doc to know precisely what is the problem
    message: "",
    description: "",
    errors: [errors]
}
```

## Credits

- http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api for most guidelines
- http://blog.octo.com/designer-une-api-rest/ some examples
