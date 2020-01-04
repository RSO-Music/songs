# Upload file 

```java
    @POST
    @Path("/{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadImage(InputStream uploadedInputStream) {

        byte[] bytes = new byte[0];
//        try (uploadedInputStream) {
//            bytes = uploadedInputStream.readAllBytes();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}
```
