# shopper
Витрина интернет-магазина


```bash
curl -i -X POST -H "Content-Type: multipart/form-data" \
  -F "title=Велосипед BMC Speedmachine" \
  -F "description=Шоссейный велосипед BMC Speedmachine 01 ONE Red AXS (2024), S, белый" \
  -F "imageFile=@example/bmc.jpg" \
  -F "price=999999.00" \
  localhost:8081/admin/items/add
```

