function initialize(entity)
    local position = entity:get(PositionComponent)
    print(position)
	if position ~= nil then
		print(position:getX())
    end
end

function update(entity)
	local position = entity:get(PositionComponent)
	position:setX(entity:get(PositionComponent):getX() + 1)
	print(position)
end