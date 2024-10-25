"use strict";
function Blog() {
    return (
        <div className="blog">
            <div>
                <img src = "https://img.freepik.com/free-photo/book-composition-with-open-book_23-2147690555.jpg"/>
            </div>
            <h2>Blog</h2>
            <p><Link to="/myBlog">Blog</Link>

                <h2>Web Development Experience</h2>
                <p>Prior to this course I had no web application or development experience. The deepest my knowledge goes is fumbling with inspect element to mess with friends and family.</p>
    
                <h2>Database Table Design</h2>
                <p>
                    The Database table i propose is for users to enter the names of their own Fantasy Worlds as well as their inspirations.
                    <ul>The following are the database field names:</ul>
        
                    <ul>
                        <li>world_id</li>
                        <li>world_name</li> 
                        <li>world_description</li>
                        <li>world_icon</li>
                        <li>world_magic_system</li>
                        <li>world_entered_or_updated</li>
                        <li>world_user_donation</li>
                        <li>world_inspiration</li>
                        <li>world_magic_level_out_of_10</li> 
                        <li>web_user_id</li>
                    </ul>
                    
                
                </p>
    
            
                <h2>Database Experience</h2>
                <p>I had an introductory course on databases before I transfered to Temple, however, this was some time ago and most assignments were simple exercises that I'd likely need a refresher on.</p>
    
                
                <h2>Homepage Module Feedback</h2>
                <p>This module seems pretty straight forward, especially after the lab assignment. I did, however, have, and might still be having, some difficulty with the MySQL portion of this HW.</p>
                <h2>Database Module Feedback</h2>
                <p>All in all, this module was pretty easy, the only tedium being the recording of the screenshots for the word document. Having some experience with databases in the past, the joining and searching for the fields wasn't an issue. Working within MySQL Workbench isn't alien either with my only issues being clarity in errors as it isn't formatted as well as the rest of the GUI.</p>
    
                <p>Click <a href="docs/Database_HW.pdf" target="_blank">here</a> link to see the accompanying Database Document </p>

                <h2> SPA Module Feedback</h2>
                <p>Since the lab was so comprehensive and Proffessor Kyvernitis provided further help during the in calss lecture, this module was easy going. The only issues I had was in centering the image on the home page well, but I was able to figure this out with some help from online resources.</p>

                <h2> Component Module Feedback</h2>
                <p>This Assignment, by far, was the most difficult. This was mostly due to its length, the testing and debugging matching it at every turn. Thankfully the plethora of supplemental material provided helped carry me through the assignment.</p>


                <h2> Web API Module Feedback</h2>
                <p> 
                    I didn't particularly find this module too difficult. While not an expert with web API's and JSON, I am somewhat familiar with it since I used it in Android  Mobile App Development. The biggest issue I had was during the lab for web API's where bad syntax for the SQL statement wasted hours of my time. I wish I had recorded images of those error then so I wouldn't have had to spend time recreating them for this assignment.
                </p>
                <p>
                    To see my <strong>List Users API</strong> open up in a new tab, click <a href="webUser/getAll" target="_blank">here</a>.
                </p>
                
                <p>
                    To see my <strong>List World API</strong> open up in a new tab, click <a href="world/getAll" target="_blank">here</a>.
                </p>
            
                <p>
                    To see my <strong>Web API Error Document</strong>, click <a href="docs/Web_API_DB_Errors.pdf" target="_blank">here</a>.
                </p>

                <h2> Show Data / AJAX Module Feedback</h2>
                <p>This Assignment technically wasn't too difficult. A majority of my issues were due to incredibly esoteric syntax errors and cache refreshes. I also spent a while going back and correcting issues with previous assignments which had a knock on effect for this one, requiring me to go back and alter several variables and string formats before moving on.</p>
                
                <h2> Logon Module Feedback</h2>
                <p> This module and its corresponding HW assignment were extremely difficult. Due to not being able to test the functionality I realised too late that the session was not saving. 
                    I also found my self at a loss when implementing the profile and logoff pages. Most assignemnts feel like logical progressions from the lab and example material, 
                    however the combination of concepts left it feeling void of information. While I will submit this code now, I hope that while presenting it I get some assistance from the TA.
                </p>

                <h2> Insert Module Feedback</h2>
                <p> After weeks of struggling with homework assignments, I finally completed a module that I feel confident in. While I did have issue and errors while programming, it was mostly due to sytax errors and careless ness when renaming variables. 
                    I even found the time to go back and fix some error from previous assignments, specifically I refactored the profile code to use React.useEffect to get the session information. All in all, the concepts of this module were straight forward and built on previous topics.
                </p>

                <h2> Update Module Feedback</h2>
                <p> I am feeling more confident with these last few assignments, as they've been building on eachother. 
                    In light of feedback I've gone back and updated a few stylings and database feedback requests. The lists should look and sort better and the database has a uniqueness requirement on world names now which the error should notify. The only issue I could see this module submission having 
                    is that when loading fields it loads the emails alphabetically first before loading the corresponding world_id's email; while this shouldn't be noticable when tested live but it cerainly is locally.
                </p>

                <h2> Delete Module Feedback</h2>
                <p> As Proffessor Kyvernitis has been saying, this last HW assignment and module was relatively short and simple. I did, however, have  some difficulty implementing the ajax component for the confirm delete"Other" and deleteUser functions and had to reference other code to make it work.
                    I came to the solution mostly by using the inspect element error messages as they pointed me to specific undefine variables that existed for the example code but not for my assignment. I also added the web_user email field into my "other"/world table list.
                </p>

                <h2> Final Project Feedback</h2>
                <p> This semester has been interesting and I certainly learned a lot. Most of the difficulties were due to my lack of reading the HW instructions clearly and java script being a nightmate when it comes to debugging and syntax errors. <br/>

                    Click <a href="docs/Final_Project_Daniel.pdf" target="_blank">here</a> to see Daniel's Final Project pdf. <br/>
                    Click <a href="docs/Final_Project_Tim.pdf" target="_blank">here</a> to see Tim's Final Project pdf. <br/>
                </p>


                <h2>Hello World API</h2>
                <p>
                    If you would like to see my "Hello World" API open up in a new tab,
                    click <a href="hello.html" target="_blank">here</a>.
                </p>
            
            </p>
        </div>
    );
}