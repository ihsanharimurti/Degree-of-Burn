# Degree of Burn: Android-Based Burn Detection Application

## Overview
**Degree of Burn** is a final year project developed by **Ihsan Harimurti**, **Bima Abizard**, and **Ratna Goeng Permadi**. This Android application facilitates easier, more accurate, and precise identification of burn injuries. It uses **Jetpack Compose** for a modern UI, integrates with **Google Cloud**, and leverages a machine learning model hosted on Google Cloud for burn detection.

> ⚠️ **Important:** This application is intended as a supportive tool for preliminary burn assessment and is **not** a substitute for professional medical advice. Always consult a healthcare provider for diagnosis and treatment of burn injuries.

## Features
- 🔥 **Burn Degree Detection**: Identifies the degree of burn from scanned images.
- 📏 **Burn Percentage Calculation**: Estimates total body surface area (TBSA) affected.
- 💧 **Fluid Resuscitation Recommendation**: Calculates fluid needs based on assessment.
- 📝 **Patient Medical Records**: Stores detailed records including burn degree, TBSA, and fluid recommendations.
- 📱 **Modern UI**: Built with Jetpack Compose for a clean and responsive experience.
- ☁️ **Google Cloud Integration**: Uses Google Cloud for database and ML model hosting.

## Technology Stack
- **Android**: Kotlin, Jetpack Compose  
- **Machine Learning**: TensorFlow (or similar, hosted on Google Cloud)  
- **Backend & Database**: Google Cloud Platform (Firestore, Cloud Functions, etc.)

## How It Works
1. **Image Input**: Users capture or upload an image of the burn.
2. **ML Analysis**: Image is sent to a cloud-hosted ML model for analysis.
3. **Assessment**: Model returns burn degree and estimated TBSA.
4. **Fluid Calculation**: Calculates fluid needs based on the results and patient data.
5. **Medical Record Generation**: Displays all data in a comprehensive patient record.
6. **Secure Storage**: Data is stored safely in Google Cloud for future reference.


## Contributors
- [Ihsan Harimurti](https://github.com/ihsanharimurti)  
- Bima Abizard Nurqolbi 
- Ratna Goeng Permadi

## License
This project is licensed under the [MIT License](LICENSE).
