
## Setup Instructions

### Prerequisites

Ensure you have the following tools and frameworks installed on your system:

- Java 17 or higher (for backend)
- Node.js 18 or higher (for frontend)
- MongoDB (for storing configurations and statistics)
- Maven (for managing backend dependencies)
- A modern browser (e.g., Chrome, Firefox)

### Backend Setup
#### Option 1: Using Command Line
1. Clone the repository:

```bash
  git clone <repository-url>
  cd RealimeTicketingSystem_backend
```
2. Build the application using Maven:

```bash
  mvn clean install
```
3. Run the application:
```bash
  java -jar target/RealimeTicketingSystem_backend-1.0.jar
```
Backend will start on http://localhost:8080.

#### Option 2: Using an IDE (e.g., IntelliJ, Eclipse)
1. Open the backend directory (ticketing-system-backend) in your preferred IDE.
2. Allow the IDE to automatically detect and import Maven dependencies.
3. Locate the main class TicketingApplication.java and run it.
4. The backend will start on http://localhost:8080.

### Frontend Setup
#### Option 1: Using Command Line
1. Navigate to the frontend directory:

```bash
  cd ../RealTimeTicketingSystem_frontend
```
2. Install dependencies:

```bash
  npm install
```
3. Start the frontend application:Start the frontend application:
```bash
  npm start
```
Backend will start on http://localhost:8080.

#### Option 2: Using an IDE or GUI Tools
1. Open the frontend directory (RealTimeTicketingSystem_frontend) in a code editor like Visual Studio Code.
2. Use the integrated terminal or GUI Tools for npm/yarn (e.g., Run or Start Script in the editor).
3. The frontend will start on http://localhost:4200.

### MongoDB Setup
#### Option 1: Using Command Line
1. Start the MongoDB server:

```bash
  mongod
```
Ensure MongoDB is running at `mongodb://localhost:27017` (default URI).

#### Option 2: Using an IDE or GUI Tools
1. Use a MongoDB GUI client like MongoDB Compass or Robo 3T.
2. Connect to the local MongoDB instance (default: `mongodb://localhost:27017`).
3. Ensure the database is active and accessible.


## Usage Instructions

### 1. Configuring the System
1. Navigate to **Configuration** in the web app.
2. Enter the required information.
3. Click **Save Configuration** to store the settings in the database (MongoDB).

### 2. Starting the Simulation
1. Click the **Start Simulation** button to initiate the system.
2. The simulation dynamically:
    - Releases tickets from vendors.
    - Processes customer ticket purchases in real-time.
3. Use the **Stop Simulation** button to halt activity.

### 3. Explanation of UI Controls
#### Configuration Panel
- Save, update, or retrieve system configurations.
- Displays validation errors for invalid inputs.

#### Simulation Controls
- **Start/Stop Simulation** buttons for managing activity.
- Real-time updates on ticket pool size and statistics.

#### Customer and Vendor Dashboards
- **Customer Statistics**: Tracks total tickets purchased and customers in a waiting state.
- **Vendor Statistics**: Displays ticket release data specific to each vendor.

#### Analytics Chart
- Visualizes ticket sales over time.
- Limited to **20 data points** for clarity.

### 4. Handling Edge Cases
- **Ticket Unavailability**: Customers are placed in a waiting state when no tickets are available.
- **Maximum Capacity**: Vendors stop releasing tickets once the maximum pool size is reached.
