const express = require('express')
const server = express();
const api_helper = require('./API_helper')
const cors = require('cors');

// TODO: Do data processing here before sending
server.use(cors())

//setting the port.
server.set('port', process.env.PORT || 5000);
var eBayKey = 'KarthikS-Summer20-PRD-e193e18a1-7274de6c';
var eBayKey2 = 'Universi-67b4-4a4e-b184-7ff2bd4b59e2';
var eBayKey3 = 'RushabhK-app-PRD-9193e215e-9f1a9ecb';
//Adding routes
server.get('/', (request, response) => {
    response.sendFile(__dirname + '/src/index.html');
});

// For search of items
server.get('/items', (request, response) => {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    let keyword = request.query.keyword;
    let minPrice = request.query.minprice;
    let maxPrice = request.query.maxprice;
    let sortOrder = request.query.sort;
    let condition = request.query.condition;

    let baseURL = "https://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + eBayKey;
    baseURL += "&paginationInput.entriesPerPage=50";
    baseURL += "&RESPONSE-DATA-FORMAT=JSON";
    baseURL += "&REST-PAYLOAD";
    let searchURL = baseURL+generateQueryParams(keyword, minPrice, maxPrice, sortOrder, condition);
    // console.log(searchURL);
    api_helper.make_API_call(searchURL)
            .then((res) => {
                //New way
                try{
                let items = res.findItemsAdvancedResponse[0].searchResult[0];
                // console.log(Number(items['@count']));
                if(Number(items['@count'])<1) {
                  response.send({});
                }
                items = items.item;
                results = [];
                for(let idx in items){
                  let item = items[idx];
                  // console.log(item);
                  let topRated = item.topRatedListing[0];
                 
                  let price = item.sellingStatus[0].convertedCurrentPrice[0].__value__;
                  let title = item.title[0];
                  let image = item.galleryURL[0];
                  let productId = item.itemId[0];
                  let product = {topRated, title, image, productId, price};

                  if(item.shippingInfo[0].shippingServiceCost!=null){
                    product.shippingInfo = item.shippingInfo[0].shippingServiceCost[0].__value__;
                  }
                  if(item.condition!=null){
                    product.condition = item.condition[0].conditionDisplayName[0];
                  }  

                  product.shipping = getShippingInfo(item.shippingInfo[0]);
                  product.shipping.push({"name":"Shipping From", "value":item.location[0]});
                  results.push(product);
                }
                // console.log(results);
                response.send({"products":results});
              } catch(e){
                console.log(e);
              }
            })
            .catch(error => {
                response.send(error);
            })
});

function getShippingInfo(data){
  let shippingInfo = [];
  if(data.shippingType != null) shippingInfo.push({"name":"shippingType", "value":data.shippingType[0]});
  if(data.shipToLocations != null) shippingInfo.push({"name":"shipToLocations", "value":data.shipToLocations[0]});
  if(data.expeditedShipping != null) shippingInfo.push({"name":"expeditedShipping", "value":data.expeditedShipping[0]});
  if(data.oneDayShippingAvailable != null) shippingInfo.push({"name":"oneDayShippingAvailable", "value":data.oneDayShippingAvailable[0]});
  if(data.handlingTime != null) shippingInfo.push({"name":"handlingTime", "value":data.handlingTime[0]});
  return shippingInfo;
}

// For detailed item access
server.get('/productInfo', (request, response) => {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    let productID = request.query.product;
    let baseURL = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid="+eBayKey+"&siteid=0&version=967&ItemID="+ 
                    productID + "&IncludeSelector=Description,Details,ItemSpecifics";
    api_helper.make_API_call(baseURL)
        .then(res => {
            try{
              // console.log(res);
              let title = res.Item.Title;
              let productId = res.Item.ItemID;
              let productUrl = res.Item.ViewItemURLForNaturalSearch;
              let productImages = res.Item.PictureURL;
              let subtitle = res.Item.Subtitle;
              let price = res.Item.ConvertedCurrentPrice.Value;
              let ItemSpecifics = [];
              for(let idx in res.Item.ItemSpecifics.NameValueList){
                let spec = res.Item.ItemSpecifics.NameValueList[idx];
                ItemSpecifics.push({"name":spec.Name, "value":spec.Value[0]});
              }
              let seller = res.Item.Seller;
              let returnPolicy = res.Item.ReturnPolicy;
              let data = {title, productId, productUrl, productImages, subtitle, price, ItemSpecifics, seller, returnPolicy};
              // console.log(data);
              response.send(data);
            } catch(e) {
              console.log(e);
            }
            // response.send(res);
        })
        .catch(error => {
            response.send(error);
        })
});

function generateQueryParams(keyword, minPrice, maxPrice, sortOrder, condition) {
    let params = "";
    params += "&keywords="+keyword;
    params += "&sortOrder="+sortOrder;
    var filterarray = []
    if(minPrice != null) filterarray.push( {"name":"MinPrice","value": minPrice,"paramName":"Currency","paramValue":"USD"});
    if(maxPrice !=null) filterarray.push( {"name":"MaxPrice","value": maxPrice,"paramName":"Currency","paramValue":"USD"});
    if(condition != null) filterarray.push( {"name":"Condition","value":condition});
    // console.log(buildConditionURL(filterarray));
    params += buildConditionURL(filterarray);
    return params;
}

function buildConditionURL(filterarray){
    urlfilter = "";
    for(var i=0; i<filterarray.length; i++) {
    //Index each item filter in filterarray
    var itemfilter = filterarray[i];
    // Iterate through each parameter in each item filter
    for(var index in itemfilter) {
      // Check to see if the paramter has a value (some don't)
      if (itemfilter[index] !== "") {
        if (itemfilter[index] instanceof Array) {
          for(var r=0; r<itemfilter[index].length; r++) {
          var value = itemfilter[index][r];
          urlfilter += "&itemFilter(" + i + ")." + index + "(" + r + ")=" + value ;
          }
        }
        else {
          urlfilter += "&itemFilter(" + i + ")." + index + "=" + itemfilter[index];
        }
      }
    }
  }
  return urlfilter;
}

//Express error handling middleware
server.use((request, response) => {
    response.type('text/plain');
    response.status(505);
    response.send('Error page');
});

//Binding to localhost://3000
server.listen(8080, () => {
    console.log('Express server started at port 8080');
});