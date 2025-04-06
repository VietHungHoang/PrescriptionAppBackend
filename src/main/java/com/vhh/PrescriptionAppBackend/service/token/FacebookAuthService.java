// package com.vhh.PrescriptionAppBackend.service.token;
// package com.yourcompany.yourapp.service;

// import com.fasterxml.jackson.annotation.JsonProperty;
// import lombok.Data; // Hoặc tự tạo Getters/Setters
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.util.UriComponentsBuilder;

// import java.util.Objects;

// @Service
// public class FacebookAuthService {

//     private static final Logger logger = LoggerFactory.getLogger(FacebookAuthService.class);
//     private static final String FACEBOOK_GRAPH_API_URL = "https://graph.facebook.com/debug_token";
//     // Endpoint để lấy thông tin user sau khi xác thực token
//     private static final String FACEBOOK_USER_INFO_URL = "https://graph.facebook.com/me";


//     private final String appId;
//     private final String appSecret;
//     private final RestTemplate restTemplate;

//     public FacebookAuthService(@Value("${facebook.appId}") String appId,
//                                @Value("${facebook.appSecret}") String appSecret) {
//         this.appId = appId;
//         this.appSecret = appSecret;
//         this.restTemplate = new RestTemplate();
//         logger.info("Facebook Auth Service initialized with App ID: {}", appId);
//     }

//     // Xác thực token với Facebook
//     public FacebookUserData verifyAccessToken(String accessToken) {
//         // Tạo App Access Token (App ID | App Secret) - KHÔNG lưu trữ cái này lâu dài
//         String appAccessToken = appId + "|" + appSecret;

//         String url = UriComponentsBuilder.fromHttpUrl(FACEBOOK_GRAPH_API_URL)
//                 .queryParam("input_token", accessToken)
//                 .queryParam("access_token", appAccessToken)
//                 .toUriString();

//         try {
//             logger.debug("Calling Facebook debug_token API...");
//             ResponseEntity<FacebookDebugTokenResponse> response = restTemplate.getForEntity(url, FacebookDebugTokenResponse.class);

//             FacebookDebugTokenData data = Objects.requireNonNull(response.getBody()).getData();

//             if (data != null && data.isValid() && data.getAppId().equals(this.appId)) {
//                 logger.info("Facebook access token verified successfully for user_id: {} and app_id: {}", data.getUserId(), data.getAppId());

//                 // Sau khi token hợp lệ, lấy thông tin cơ bản của user (tên, email nếu có quyền)
//                  return fetchFacebookUserInfo(accessToken, data.getUserId());

//             } else {
//                 logger.warn("Invalid Facebook access token or app_id mismatch. IsValid: {}, TokenAppId: {}, ExpectedAppId: {}",
//                         data != null ? data.isValid() : "null",
//                         data != null ? data.getAppId() : "null",
//                         this.appId);
//                 return null;
//             }
//         } catch (HttpClientErrorException e) {
//             logger.error("Error calling Facebook debug_token API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
//             return null;
//         } catch (Exception e) {
//             logger.error("Unexpected error verifying Facebook token: {}", e.getMessage(), e);
//             return null;
//         }
//     }

//      // Lấy thông tin người dùng từ Facebook Graph API
//      private FacebookUserData fetchFacebookUserInfo(String userAccessToken, String facebookUserId) {
//          String userInfoUrl = UriComponentsBuilder.fromHttpUrl(FACEBOOK_USER_INFO_URL)
//                  .queryParam("fields", "id,name,email,picture") // Yêu cầu các trường cần thiết
//                  .queryParam("access_token", userAccessToken)
//                  .toUriString();
//          try {
//              logger.debug("Fetching user info from Facebook Graph API for user_id: {}", facebookUserId);
//              ResponseEntity<FacebookUserData> response = restTemplate.getForEntity(userInfoUrl, FacebookUserData.class);
//              FacebookUserData userData = response.getBody();
//              if (userData != null && facebookUserId.equals(userData.getId())) {
//                  logger.info("Successfully fetched Facebook user info for ID: {}", userData.getId());
//                  return userData;
//              } else {
//                   logger.warn("Failed to fetch Facebook user info or ID mismatch.");
//                   // Có thể trả về một đối tượng chỉ có ID nếu cần
//                   FacebookUserData basicData = new FacebookUserData();
//                   basicData.setId(facebookUserId);
//                   return basicData; // Trả về ít nhất ID nếu API /me thất bại
//              }
//          } catch (HttpClientErrorException e) {
//              logger.error("Error fetching Facebook user info: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
//               FacebookUserData basicData = new FacebookUserData(); // Trả về ID nếu lỗi
//               basicData.setId(facebookUserId);
//               return basicData;
//          } catch (Exception e) {
//               logger.error("Unexpected error fetching Facebook user info: {}", e.getMessage(), e);
//               FacebookUserData basicData = new FacebookUserData(); // Trả về ID nếu lỗi
//               basicData.setId(facebookUserId);
//               return basicData;
//          }
//      }

//     // --- DTOs nội bộ cho Facebook Response ---
//     @Data // Lombok hoặc tự viết Getters/Setters
//     private static class FacebookDebugTokenResponse {
//         private FacebookDebugTokenData data;
//     }

//     @Data
//     private static class FacebookDebugTokenData {
//         @JsonProperty("app_id")
//         private String appId;
//         private String type;
//         private String application;
//         @JsonProperty("expires_at")
//         private long expiresAt;
//         @JsonProperty("is_valid")
//         private boolean isValid;
//         @JsonProperty("issued_at")
//         private long issuedAt;
//         // metadata; // Có thể thêm nếu cần
//         // scopes; // Có thể thêm nếu cần
//         @JsonProperty("user_id")
//         private String userId;
//     }

//     @Data // Dùng để trả về thông tin user đã lấy được
//     public static class FacebookUserData {
//          private String id;
//          private String name;
//          private String email; // Có thể null nếu user không cấp quyền
//          private FacebookPictureData picture; // Ảnh đại diện
//     }

//      @Data
//      public static class FacebookPictureData {
//          private FacebookPicture pictureData;

//          @JsonProperty("data") // Đổi tên thuộc tính JSON
//          public FacebookPicture getPictureData() { return pictureData; }
//          @JsonProperty("data")
//          public void setPictureData(FacebookPicture pictureData) { this.pictureData = pictureData; }
//      }

//      @Data
//      public static class FacebookPicture {
//          private int height;
//          private int width;
//          @JsonProperty("is_silhouette")
//          private boolean isSilhouette;
//          private String url;
//      }
// }
