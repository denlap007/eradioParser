<h1>DEVELOPMENT CHANGES FROM V.1.0 to v1.1</h1>

<hr />

<h2>Added Class <em>DefaultCaller</em>.</h2>

<p>This Class holds all the static methods that are used from all the other Classes. In addition, it holds 
all the important information such as:
filePath if set from command line. 'filePath'
Initial Urls loaded from command line or from the menu. 'theUrls'
Final radio station titles and urls in that order. 'eradioLinks'</p>

<p>All methods and variables of this Class are static.</p>

<h2>Removed all <em>static</em> methods from all Classes.</h2>

<p>Added to DefaultCaller instead.</p>

<h2>Removed (moved to DefaultCaller or changed to private) all static variables from all Classes.</h2>

<h2>Getters and Setters are now there for all Class private members.</h2>

<h2>Changed the way links are Loaded from the Menu!</h2>

<p>Element &lt;div&gt; with "id=paneContainer" is parsed instead of &lt;div&gt; with "class=menuOptions" in order to get
the category and location links. That results to adding the Cyprus location that previously wasn't included.</p>

<h2>Recursive method <em>parseUrl()</em> is called every time a url is passed into a DOM document!</h2>

<p>That way, it is ensured the stability of the program, as Exceptions are handled by recursively calling the method
MAX_CONNECTION_ATTEMPTS times. If a url cannot be passed to a DOM document due to some error, a message 
is printed and it is bypassed, without forcing the program to crash! That addition resolved many stability 
issues that could arise at some (more rare though) time!</p>

<p>Also, the method was slightly changed by making a connection first and setting the user agent and then passing 
the html to a DOM document.</p>
