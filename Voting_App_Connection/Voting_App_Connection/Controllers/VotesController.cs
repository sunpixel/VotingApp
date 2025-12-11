using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Voting_App_Connection.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class VotesController : ControllerBase
    {
        public AppDbContext _Context { get; set; }

        public VotesController(AppDbContext context) 
        {
            _Context = context;
        }


        [HttpGet]
        public async Task<ActionResult<List<Vote>>> GetAllResults()
        {
            return await _Context.Votes!.ToListAsync();
        }

        [HttpPost]
        public async Task<ActionResult> PostVote(PostVote data)
        {
            if (data == null) return BadRequest();

            var db_data = await _Context.Votes!.FirstOrDefaultAsync(u => u.Id == data.Id);

            if (db_data == null) return NotFound();

            var voters = db_data.WhoVoted;

            if (voters!.Length != 0)
            {
                if (voters!.Any(data.Device_Id!.Contains))
                {
                    return Conflict("Already voted for this");
                }
            }

            voters.Append(data.Device_Id);

            db_data.NumberOfVotes = voters.Length;

            db_data.WhoVoted = voters;

            _Context.Votes!.Update(db_data);
            await _Context.SaveChangesAsync();

            return Ok("Voted");
        }

    }

    public class PostVote
    {
        public int Id { get; set; }
        public string? Device_Id { get; set; }
    }

}
