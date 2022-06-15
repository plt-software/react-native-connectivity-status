Pod::Spec.new do |s|
  s.name         = "react-native-connectivity-status"
  s.version      = "2.0.0"
  s.summary      = "A ReactNative module to check Location status on Android and iOS"
  s.description  = <<-DESC
                  A ReactNative module to check Location status on Android and iOS
                   DESC
  s.homepage     = "https://www.npmjs.com/package/react-native-connectivity-status"
  s.license      = { :type => "MIT", :file => "LICENSE.md" }
  s.author       = { "Mattia Panzeri" => "mattia.panzeri93@gmail.com" }
  s.platform     = :ios, "8.0"
  s.source       = { :git => "https://github.com/nearit/react-native-connectivity-status.git", :tag => "master" }
  s.source_files  = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
end

