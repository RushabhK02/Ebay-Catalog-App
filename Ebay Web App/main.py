from flask import Flask, request, jsonify
import requests
import json

app = Flask(__name__)
app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 10 # set max age to 10 seconds
key = 'Universi-67b4-4a4e-b184-7ff2bd4b59e2'

@app.after_request
def after_request(resp):
    resp.headers["Cache-Control"] = "no-store"
    resp.headers["Pragma"] = "no-cache"
    return resp

@app.route('/')
def sendHomePage():
    return app.send_static_file("home.html")

@app.route('/items')
def getItems():
     keyword = request.args.get('keyword')
     minPrice = request.args.get('minprice')
     maxPrice = request.args.get('maxprice')
     returnAccepted = request.args.get('return')
     sortOrder = request.args.get('sort')
     condition = request.args.get('condition')
     freeShipping = request.args.get('free-shipping')
     expeditedShipping = request.args.get('expedited-shipping')
     if condition!=None:
        conditionArray = condition.split(',')
     else:
        conditionArray = []
     filterarray = []
     if maxPrice!=None: 
         filterarray.append({"name":"MaxPrice","value":maxPrice,"paramName":"Currency","paramValue":"USD"})
     if minPrice!=None: 
         filterarray.append({"name":"MinPrice","value":minPrice,"paramName":"Currency","paramValue":"USD"})
     if returnAccepted!=None:
         filterarray.append({"name":"ReturnsAcceptedOnly","value":returnAccepted})
     if condition!=None: 
         if len(conditionArray)>1:
            filterarray.append({"name":"Condition","value":conditionArray})
         else:
            filterarray.append({"name":"Condition","value":condition})
     if freeShipping!=None: 
         filterarray.append({"name":"FreeShippingOnly", "value":freeShipping})
     if expeditedShipping!=None:
         filterarray.append({"name":"ExpeditedShippingType", "value":"Expedited"})

     url = "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0"
     url += "&SECURITY-APPNAME=RushabhK-app-PRD-9193e215e-9f1a9ecb"
     url += "&RESPONSE-DATA-FORMAT=JSON"
     url += "&REST-PAYLOAD"
     url += "&keywords="+keyword
     url += "&sortOrder="+sortOrder
     url += "&paginationInput.entriesPerPage=50"
     urlfilters = buildURLArray(filterarray)
     urlWithFilters = url + urlfilters
    
     response = requests.get(urlWithFilters)
     json_data = response.json()
     result = []
     items = json_data["findItemsAdvancedResponse"][0]
     print(items)
     ack = items["ack"][0]
     paginationOutput = items["paginationOutput"][0]
     searchResults = items["searchResult"][0]
     count = searchResults["@count"]
     if count != "0":
        ebayItems = searchResults["item"]
     else:
        ebayItems = []
     for i,item in enumerate(ebayItems): 
         if "title" not in item:
             continue
         if "primaryCategory" not in item:
             continue
         if "galleryURL" not in item:
             continue
         if "viewItemURL" not in item:
             continue
         if "location" not in item:
             continue
         if "shippingInfo" not in item:
             continue
         if "sellingStatus" not in item:
             continue
         if "returnsAccepted" not in item:
             continue
         if "condition" not in item:
             continue
         if "topRatedListing" not in item:
             continue
         shippingInfo = item["shippingInfo"][0]
         if "shippingServiceCost" not in shippingInfo:
             continue
         result.append(item)
         if len(result) == 10:
             break
     
     data = jsonify({ "paginationOutput": paginationOutput, "ack": ack, "searchResult": result})
     return data


#  Generates an indexed URL snippet from the array of item filters
def buildURLArray(filterarray):
    urlfilter = ''
#   Iterate through each filter in the array
    for i,itemfilter in enumerate(filterarray): 
    #  Iterate through each parameter in each item filter
        for (index,value) in itemfilter.items() :
    #  Check to see if the paramter has a value (some don't)
            if index != "": 
                if isinstance(value, list):
                    for r,elem in enumerate(value):
                        urlfilter += "&itemFilter(" + str(i) + ")." + index + "(" + str(r) + ")=" + elem
                else:
                    urlfilter += "&itemFilter(" + str(i) + ")." + index + "=" + value
    
    return urlfilter

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)

