package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"google.golang.org/appengine/v2"
	"google.golang.org/appengine/v2/user"
)

// https://cloud.google.com/appengine/docs/standard/go/runtime#environment_variables
const PORT = "PORT"

const GOOGLE_CLOUD_PROJECT = "GOOGLE_CLOUD_PROJECT" // The Google Cloud project ID associated with your application.
const GAE_APPLICATION = "GAE_APPLICATION"           // App id, with prefix.
const GAE_ENV = "GAE_ENV"                           // `standard` in production.
const GAE_RUNTIME = "GAE_RUNTIME"                   // Runtime in `app.yaml`.
const GAE_VERSION = "GAE_VERSION"                   // App version.
const DUMMY_APP_ID = "my-app-id"

func init() {
	// Register handlers in init() per `appengine.Main()` documentation.
	http.HandleFunc("/", indexHandler)
}

func main() {
	if isDev() {
		_ = os.Setenv(GAE_APPLICATION, DUMMY_APP_ID)
		_ = os.Setenv(GAE_RUNTIME, "go123456")
		_ = os.Setenv(GAE_VERSION, "my-version")
		_ = os.Setenv(GAE_ENV, "standard")
		_ = os.Setenv(PORT, "4200")
		log.Printf("appengine.Main() will listen: %s", defaultVersionOrigin())
	}

	// Standard App Engine APIs require `appengine.Main` to have been called.
	appengine.Main()
}

func defaultVersionOrigin() string {
	if isDev() {
		return "http://localhost:" + os.Getenv(PORT)
	} else {
		return fmt.Sprintf("https://%s.appspot.com", projectID())
	}
}

func projectID() string {
	return os.Getenv(GOOGLE_CLOUD_PROJECT)
}

func isDev() bool {
	appid := os.Getenv(GAE_APPLICATION)
	return appid == "" || appid == DUMMY_APP_ID
}

func indexHandler(w http.ResponseWriter, r *http.Request) {
	// App Engine context for the in-flight HTTP request.
	ctx := appengine.NewContext(r)

	w.Header().Set("Content-Type", "text/plain")

	for name, headers := range r.Header {
		for _, h := range headers {
			msg := fmt.Sprintf("%v: %v", name, h)
			fmt.Fprintf(w, "%s\n", msg)
			log.Print(msg)
		}
	}

	fmt.Fprintln(w, "--------------------------------------")

	userLoggedIn := user.Current(ctx) != nil
	fmt.Printf("userLoggedIn: %v\n", userLoggedIn)

	if userLoggedIn {
		currentUser := user.Current(ctx)
		fmt.Fprintf(w, "currentUser : %v\n", currentUser)
		fmt.Fprintf(w, "currentUser.Admin : %v\n", currentUser.Admin)
		fmt.Fprintf(w, "currentUser.AuthDomain : %v\n", currentUser.AuthDomain)
		fmt.Fprintf(w, "currentUser.ClientID, : %v\n", currentUser.ClientID)
		fmt.Fprintf(w, "currentUser.Email : %v\n", currentUser.Email)
		fmt.Fprintf(w, "currentUser.FederatedIdentity : %v\n", currentUser.FederatedIdentity)
		fmt.Fprintf(w, "currentUser.FederatedProvider : %v\n", currentUser.FederatedProvider)
		fmt.Fprintf(w, "currentUser.ID, : %v\n", currentUser.ID)
	} else {
		loginURL, err := user.LoginURL(ctx, r.URL.String())
		if err != nil {
			fmt.Fprintf(w, "** ERROR generating loginURL**: %v", err)
		} else {
			fmt.Fprintf(w, "loginURL: %v\n", loginURL)
		}
	}

	fmt.Fprintln(w, "--------------------------------------")
}
