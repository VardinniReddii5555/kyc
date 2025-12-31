@PostMapping("/submit")
public String submitKyc(@RequestParam("videoData") String videoData,
HttpSession session) throws IOException {
String base64Video = videoData.split(",")[1];
byte[] videoBytes = Base64.getDecoder().decode(base64Video);

String username = (String) session.getAttribute("username");
String fileName = "kyc_" + username + ".webm";

File file = new File("C:/kyc_videos/" + fileName);
Files.write(file.toPath(), videoBytes);
session.setAttribute("videoFileName", fileName);

return "success";
}
