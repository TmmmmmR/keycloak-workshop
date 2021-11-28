<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
    <head>
        <title>Photo Detail Page</title>
        <link rel="stylesheet" type="text/css" href="/styles.css"/>
    </head>
    <body>
        <div class="wrapper" id="profile">
            <div class="menu">
                <button name="backBtn" onclick="location.href = '/app'">Back</button>
            </div>

            <div class="content">
                <div id="profile-header" class="header">
                    Photo details
                </div>

                <div id="profile-content" class="message">
                    <table cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="label">Name</td>
                            <td><span id="photoname">${photo.name}</span></td>
                        </tr>
                        <tr class="even">
                            <td class="label">Owner</td>
                            <td><span id="username">${photo.owner.username}</span></td>
                        </tr>
                        <tr>
                            <td class="label" colspan=2><img width="50%" src="/app/img/${photo.id}" /></td>
                        </tr>
                    </table>
                </div>
            </div>

        </div>

    </body>
</html>