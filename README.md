# PipelineCICD

This repository contains the code and configurations for a Continuous Integration and Continuous Deployment (CI/CD) pipeline ** Backend SpringBoot **.

![CI/CD Pipeline Backend SpringBoot](https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/790e78d3-e302-4db8-99cb-3963ddf2bc2a)


## Table of Contents

- [About the Project](#about-the-project)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [Contact](#contact)

## About the Project

This project demonstrates a CI/CD pipeline setup using various tools and technologies. It includes Docker configurations, Kubernetes deployment files, and CI/CD scripts.

<img width="1582" alt="deployedMysql" src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/7373fccb-7849-4753-8518-750926219fd2">
<img width="1591" alt="deployedbackend" src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/be17a65b-e65c-438e-b716-00b451915aba">
<img width="1579" alt="deployed" src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/51fe782f-4aa1-49a6-a8a0-453d0f279995">
<img width="1888" alt="connected in cluster " src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/a01cab81-9d82-4dc8-b591-6d840f521f4c">
<img width="1792" alt="checkout" src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/61b37369-69e2-4013-a300-74bfb688f17d">
<img width="1792" alt="buildandpushbackend" src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/9fed4e47-fbaa-49bc-9506-56dc82f4cf31">
<img width="1768" alt="buildanddeploySuccess" src="https://github.com/ChawkiMersani/pipelineCICD/assets/100194333/00fbc47d-8dda-43d8-9cee-2cdbf6c1ec18">

## Technologies Used

- Java
- Docker
- Kubernetes
- GitHub Actions

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- Docker
- Kubernetes
- Maven
- Git

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/ChawkiMersani/pipelineCICD.git
2. Navigate to the project directory:
   ```sh
   cd pipelineCICD
3. Build the project using Maven:
   ```sh
   ./mvnw clean install
### Usage
1. Building Docker Images
   ```sh
   docker build -t your-image-name
2. Deploying to Kubernetes
   ```sh
   kubectl apply -f spring_dep.yml

### Contact 

Chawki Mersani - chawki.marseni@gmail.com


 



